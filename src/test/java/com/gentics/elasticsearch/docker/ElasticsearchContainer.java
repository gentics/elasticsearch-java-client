package com.gentics.elasticsearch.docker;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.RemoteDockerImage;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.LazyFuture;

/**
 * Testcontainer for a non-clustered Elasticsearch instance.
 */
public class ElasticsearchContainer extends GenericContainer<ElasticsearchContainer> {

	public static final String VERSION = "6.8.0";
	private boolean withIngest = false;
	private boolean withSSL = false;

	public ElasticsearchContainer() {
		this(false);
	}

	public ElasticsearchContainer(boolean withIngest) {
		super(prepareDockerImage(withIngest));
		this.withIngest = withIngest;
	}

	private static LazyFuture<String> prepareDockerImage(boolean withIngest) {
		if (withIngest) {
			try {
				ImageFromDockerfile dockerImage = new ImageFromDockerfile("elasticsearch", false);
				String dockerFile = IOUtils.toString(ElasticsearchContainer.class.getResourceAsStream("/elasticsearch/Dockerfile"),
					StandardCharsets.UTF_8);
				dockerFile = dockerFile.replace("%VERSION%", VERSION);
				dockerImage.withFileFromString("Dockerfile", dockerFile);
				return dockerImage;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return new RemoteDockerImage("docker.elastic.co/elasticsearch/elasticsearch:" + VERSION);
		}
	}

	@Override
	protected void configure() {
		addEnv("discovery.type", "single-node");
		addEnv("bootstrap.memory_lock", "true");
		addEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

		if (!withIngest) {
			addEnv("node.ingest", "false");
		}
		if (withSSL) {
			addEnv("xpack.security.enabled", "true");
			addEnv("xpack.security.http.ssl.enabled", "true");
			addEnv("xpack.security.http.ssl.key", "certs/elastic-certificates.key.pem");
			addEnv("xpack.security.http.ssl.certificate", "certs/elastic-certificates.crt.pem");
			addEnv("xpack.security.http.ssl.certificate_authorities", "certs/elastic-stack-ca.crt.pem");
		}

		withLogConsumer(c -> {
			System.out.print(c.getUtf8String());
		});

		withFileSystemBind(new File("src/test/resources/certs").getAbsolutePath(), "/usr/share/elasticsearch/config/certs/", BindMode.READ_ONLY);
		withTmpFs(Collections.singletonMap("/usr/share/elasticsearch/data", "rw"));

		withExposedPorts(9200);
		withStartupTimeout(Duration.ofSeconds(250L));
		if (withSSL) {
			// waitingFor(Wait.forHttps("/"));
			waitingFor(Wait.forLogMessage(".*Node.*started.*", 1));
		} else {
			waitingFor(Wait.forHttp("/"));
		}

	}

	public ElasticsearchContainer withIngest() {
		this.withIngest = true;
		return this;
	}

	public ElasticsearchContainer withSSL() {
		this.withSSL = true;
		return this;
	}

	public String generateRandomPassword() throws UnsupportedOperationException, IOException, InterruptedException {
		final String PREFIX = "PASSWORD elastic = ";
		ExecResult result = execInContainer("/usr/share/elasticsearch/bin/elasticsearch-setup-passwords",
			"auto",
			"-b",
			"--url",
			"https://127.0.0.1:9200",
			"-E", "xpack.security.http.ssl.key=certs/elastic-certificates.key.pem",
			"-E", "xpack.security.http.ssl.certificate=certs/elastic-certificates.crt.pem",
			"-E", "xpack.security.http.ssl.certificate_authorities=certs/elastic-stack-ca.crt.pem");
		if (result.getExitCode() != 0) {
			System.err.println(result.getStdout());
			System.err.println(result.getStderr());
			fail("The passwords could not be generated.");
		}
		String stdout = result.getStdout();
		try (Scanner scanner = new Scanner(stdout)) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				int idx = line.indexOf(PREFIX);
				if (idx != -1) {
					return line.substring(PREFIX.length());
				}
			}
		}
		throw new RuntimeException("Password could not be found.");
	}

}
