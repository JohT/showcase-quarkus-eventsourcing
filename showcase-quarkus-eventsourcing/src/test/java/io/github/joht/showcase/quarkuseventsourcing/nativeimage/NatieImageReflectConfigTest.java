package io.github.joht.showcase.quarkuseventsourcing.nativeimage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * This test compares the entries of the generated
 * <code>native-image/reflect-config.json</code> with the main
 * <code>reflection-config.json</code> in the root directory.
 * <p>
 * This test is currently used as a tool to compare reflection-config and trace-files,
 * since a stable process to generate a reflection-config hadn't been found yet.
 * 
 * <code>native-image/reflect-config.json</code> is generated using
 * <code>$GRAALVM_HOME/bin/java -agentlib:native-image-agent=config-output-dir=native-image -jar ./target/showcase-quarkus-eventsourcing-1.0-SNAPSHOT-runner.jar</code>
 * 
 * @author JohT
 */
class NatieImageReflectConfigTest {
	private static final Jsonb create = JsonbBuilder.create();

	@Test
	void missingInManualEditedFile() {
		Set<String> mainConfig = classNamesRegisteredForReflections("reflection-config.json");
		Set<String> generatedConfig = classNamesRegisteredForReflections("native-image/432-reflect-config.json");
		generatedConfig.removeAll(mainConfig);
		String axonClasses = generatedConfig.stream()
				// .filter(name -> name.startsWith("org.axonframework"))
				// .filter(name ->
				// !classForName(name).map(Class::isInterface).orElse(Boolean.FALSE))
				.collect(Collectors.joining("\n"));
		System.out.println("missingInManualEditedFile");
		System.out.println(axonClasses);
	}

	@Test
	void missingInGeneratedFile() {
		Set<String> mainConfig = classNamesRegisteredForReflections("reflection-config.json");
		Set<String> generatedConfig = classNamesRegisteredForReflections("native-image/432-reflect-config.json");
		mainConfig.removeAll(generatedConfig);
		System.out.println("missingInGeneratedFile");
		System.out.println(mainConfig.stream().collect(Collectors.joining("\n")));
	}

	@Disabled
	@Test
	void traceFilteredAndSorted() {
		List<String> lines = readLines("native-image/trace-axon-422.json");
		Collections.sort(lines);
		writeLines("native-image/trace-axon-422-filtered-sorted.json", lines);
	}

	@SuppressWarnings("unchecked")
	private Set<String> classNamesRegisteredForReflections(String filename) {
		try (FileReader fileReader = new FileReader(filename)) {
			List<Map<String, Object>> config = create.fromJson(fileReader, List.class);
			return config.stream().map(map -> (String) map.get("name")).collect(Collectors.toSet());
		} catch (FileNotFoundException e) {
			return Collections.emptySet();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private List<String> readLines(String fileName) {
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			return stream.filter(line -> line.contains("org.axon")).sorted().distinct().collect(Collectors.toList());
		} catch (FileNotFoundException e) {
			return Collections.emptyList();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void writeLines(String fileName, Iterable<String> lines) {
		try {
			Files.write(Paths.get(fileName), lines, StandardOpenOption.CREATE);
		} catch (FileNotFoundException e) {
			// Nothing.
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}