/*
 * Copyright 2020 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.accountgenerator.generator.cavium;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static tech.pegasys.accountgenerator.generator.cavium.CaviumSubCommand.COMMAND_NAME;

import tech.pegasys.accountgenerator.AccountGeneratorBaseCommand;
import tech.pegasys.accountgenerator.AccountGeneratorSubCommand;
import tech.pegasys.accountgenerator.CommandlineParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine;

class CaviumSubCommandTest {

  @TempDir static Path tempDir;

  protected final StringWriter commandOutput = new StringWriter();
  protected final StringWriter commandError = new StringWriter();
  protected final PrintWriter outputWriter = new PrintWriter(commandOutput, true);
  protected final PrintWriter errorWriter = new PrintWriter(commandError, true);

  protected AccountGeneratorBaseCommand config;
  protected CommandlineParser parser;
  protected AccountGeneratorSubCommand subCommand;
  protected String defaultUsageText;
  protected String subCommandUsageText;

  @BeforeEach
  void setup() {
    subCommand = subCommand();
    config = new AccountGeneratorBaseCommand();
    parser = new CommandlineParser(config, outputWriter, errorWriter);
    parser.registerGenerator(subCommand);

    final CommandLine commandLine = new CommandLine(new AccountGeneratorBaseCommand());
    commandLine.addSubcommand(subCommand.getCommandName(), subCommand);
    defaultUsageText = commandLine.getUsageMessage();
    subCommandUsageText =
        commandLine.getSubcommands().get(subCommand.getCommandName()).getUsageMessage();
  }

  protected CaviumSubCommand subCommand() {
    return new CaviumSubCommand() {
      @Override
      public void run() {
        // we only want to perform validation in these unit test cases
        validateArgs();
      }
    };
  }

  @ParameterizedTest
  @ValueSource(strings = {"--config", "-c"})
  void parseCommandSuccessWithConfig(final String subCommandOption) {
    final Path expectedPath = Path.of("/keys/directory/path/to/config/file");
    final List<String> subCommandOptions = List.of(subCommandOption, expectedPath.toString());
    final List<String> options = getOptions(subCommandOptions);
    final boolean result = parser.parseCommandLine(options.toArray(String[]::new));
    assertThat(result).isTrue();
    assertThat(((CaviumSubCommand) subCommand).getConfig()).isEqualTo(expectedPath);
  }

  @Test
  void parseCommandFailsWithoutConfig() {
    final List<String> subCommandOptions = new ArrayList<>();
    final List<String> options = getOptions(subCommandOptions);
    final boolean result = parser.parseCommandLine(options.toArray(String[]::new));
    assertThat(result).isFalse();
    assertThat(((CaviumSubCommand) subCommand).getConfig()).isNull();
  }

  @Test
  void parseTomlSuccess() {
    Path configPath = tempDir.resolve("accountgenerator-config-cavium.toml");
    createCaviumTomlFileAt(configPath);
    final List<String> subCommandOptions = List.of("--config", configPath.toString());
    final List<String> options = getOptions(subCommandOptions);
    final boolean result = parser.parseCommandLine(options.toArray(String[]::new));
    assertThat(result).isTrue();
    // subCommand.createGeneratorFactory(tempDir);
  }

  private List<String> getOptions(final List<String> subCommandOptions) {
    final Map<String, Object> options = new LinkedHashMap<>();
    options.put("directory", "/keys/directory");
    final List<String> cmdLine = new ArrayList<>();
    options.forEach((option, value) -> cmdLine.add("--" + option + "=" + value));
    cmdLine.add(COMMAND_NAME);
    cmdLine.addAll(subCommandOptions);
    return cmdLine;
  }

  public void createCaviumTomlFileAt(final Path tomlPath) {
    final StringBuilder sb = new StringBuilder();
    sb.append(String.format("[%s]\n", "cavium-generator"));
    sb.append(String.format("%s = \"%s\"\n", "library", "/opt/cloudhsm/lib/libcloudhsm_pkcs11.so"));
    sb.append(String.format("%s = \"%s\"\n", "pin", "alice:391019314"));
    sb.append(String.format("%s = \"%s\"\n", "sas", "/opt/accountgenerator/scripts/sas.sh"));
    final String toml = sb.toString();
    createTomlFile(tomlPath, toml);
  }

  private void createTomlFile(final Path tomlPath, final String toml) {
    try {
      Files.writeString(tomlPath, toml);
    } catch (final IOException e) {
      fail("Unable to create TOML file.");
    }
  }
}
