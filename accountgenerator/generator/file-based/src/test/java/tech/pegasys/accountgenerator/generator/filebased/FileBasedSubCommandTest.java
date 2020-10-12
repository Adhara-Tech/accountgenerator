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
package tech.pegasys.accountgenerator.generator.filebased;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.pegasys.accountgenerator.generator.filebased.FileBasedSubCommand.COMMAND_NAME;

import tech.pegasys.accountgenerator.AccountGeneratorBaseCommand;
import tech.pegasys.accountgenerator.AccountGeneratorSubCommand;
import tech.pegasys.accountgenerator.CommandlineParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine;

public class FileBasedSubCommandTest {

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

  protected FileBasedSubCommand subCommand() {
    return new FileBasedSubCommand() {
      @Override
      public void run() {
        // we only want to perform validation in these unit test cases
        validateArgs();
      }
    };
  }

  @ParameterizedTest
  @ValueSource(strings = {"--password-file", "-p"})
  void parseCommandSuccessWithPasswordFile(final String subCommandOption) {
    final Path expectedPath = Path.of("/keys/directory/path/to/password/file");
    final List<String> subCommandOptions = List.of(subCommandOption, expectedPath.toString());
    final List<String> options = getOptions(subCommandOptions);
    final boolean result = parser.parseCommandLine(options.toArray(String[]::new));
    assertThat(result).isTrue();
  }

  @Test
  void parseCommandFailsWithoutPasswordFile() {
    final List<String> subCommandOptions = new ArrayList<>();
    final List<String> options = getOptions(subCommandOptions);
    final boolean result = parser.parseCommandLine(options.toArray(String[]::new));
    assertThat(result).isFalse();
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
}
