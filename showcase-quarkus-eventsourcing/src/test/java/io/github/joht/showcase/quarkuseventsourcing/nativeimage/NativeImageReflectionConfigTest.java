package io.github.joht.showcase.quarkuseventsourcing.nativeimage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

/**
 * This test compares the entries of the generated
 * <code>native-image/reflect-config.json</code> with the current
 * <code>reflection-config.json</code> in the root directory.
 * <p>
 * This test is used as a tool to compare reflection-config and trace-files.
 * 
 * <code>native-image/reflect-config.json</code> is generated using
 * <code>$GRAALVM_HOME/bin/java -agentlib:native-image-agent=config-output-dir=native-image -jar ./target/showcase-quarkus-eventsourcing-1.0-SNAPSHOT-runner.jar</code>
 * 
 * @author JohT
 */
@Disabled
class NativeImageReflectionConfigTest {

	private static final String CURRENT_REFLECTION_CONFIG = "reflection-config.json";
	private static final String GENERATED_REFLECTION_CONFIG = "native-image-agent-results/java17-axon-4-6-2-quarkus-2-13-4-Final-graalvm-22-3-0/reflect-config.json";
	private static final Jsonb json = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

	@Test
	void missingInCurrentConfigurationdFile() {
		Set<String> currentConfig = classNamesRegisteredForReflections(CURRENT_REFLECTION_CONFIG);
		Set<String> generatedConfig = classNamesRegisteredForReflections(GENERATED_REFLECTION_CONFIG);
		generatedConfig.removeAll(currentConfig);
		String axonClasses = generatedConfig.stream()
				// .filter(name -> name.startsWith("org.axonframework"))
				// .filter(name ->
				// !classForName(name).map(Class::isInterface).orElse(Boolean.FALSE))
				.collect(Collectors.joining("\n"));
		System.out.println("\nClass names that are contained in the generated configuration file and that are missing in the current configuration file:\n");
		System.out.println(axonClasses);
	}

	@Test
	void additionalInCurrentConfigurationFile() {
		Set<String> currentConfig = classNamesRegisteredForReflections(CURRENT_REFLECTION_CONFIG);
		Set<String> generatedConfig = classNamesRegisteredForReflections(GENERATED_REFLECTION_CONFIG);
		currentConfig.removeAll(generatedConfig);
		System.out.println("\nAdditional but probably not necessary class names in the current configuration file:\n");
		System.out.println(currentConfig.stream().collect(Collectors.joining("\n")));
	}

	@Test
	void removeClassNamesThatArentContainedInTheGeneratedFile() {
		Set<String> currentConfig = classNamesRegisteredForReflections(CURRENT_REFLECTION_CONFIG);
		Set<String> generatedConfig = classNamesRegisteredForReflections(GENERATED_REFLECTION_CONFIG);
		currentConfig.removeAll(generatedConfig);
		String jsonWithNecessaryClasses = jsonWithRemovedClassEntries(CURRENT_REFLECTION_CONFIG, currentConfig);
		writeLines(CURRENT_REFLECTION_CONFIG + "-removed.json", Collections.singleton(jsonWithNecessaryClasses));
	}

	@Disabled
	@Test
	void traceFilteredAndSorted() {
		List<String> lines = readLinesFiltered(GENERATED_REFLECTION_CONFIG, "org.axon");
		Collections.sort(lines);
		writeLines(GENERATED_REFLECTION_CONFIG + "-filtered-sorted.json", lines);
	}

	@SuppressWarnings("unchecked")
	private Set<String> classNamesRegisteredForReflections(String filename) {
		try (FileReader fileReader = new FileReader(filename)) {
			List<Map<String, Object>> config = json.fromJson(fileReader, List.class);
			return config.stream().map(map -> map.get("name")).map(String.class::cast).collect(Collectors.toSet());
		} catch (FileNotFoundException e) {
			return Collections.emptySet();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private String jsonWithRemovedClassEntries(String filename, Collection<String> classNamesToRemove) {
		try (FileReader fileReader = new FileReader(filename)) {
			List<Map<String, Object>> config = json.fromJson(fileReader, List.class);
			List<Map<String, Object>> filtered = config.stream()
					.filter(map -> !classNamesToRemove.contains(Objects.toString(map.get("name"))))
					.collect(Collectors.toList());
			return json.toJson(filtered);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private List<String> readLinesFiltered(String fileName, String lineContainsFilter) {
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			return stream.filter(line -> line.contains(lineContainsFilter)).sorted().distinct().collect(Collectors.toList());
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