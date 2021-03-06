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
package tech.pegasys.accountgenerator.generator.hsm;

import static tech.pegasys.accountgenerator.DefaultCommandValues.MANDATORY_PATH_FORMAT_HELP;
import static tech.pegasys.accountgenerator.RequiredOptionsUtil.checkIfRequiredOptionsAreInitialized;

import tech.pegasys.accountgenerator.AccountGeneratorSubCommand;
import tech.pegasys.accountgenerator.KeyGeneratorInitializationException;
import tech.pegasys.accountgenerator.TomlTableAdapter;
import tech.pegasys.accountgenerator.core.KeyGeneratorProvider;

import java.nio.file.Path;
import java.util.Optional;

import com.google.common.base.MoreObjects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tuweni.toml.TomlParseResult;
import org.apache.tuweni.toml.TomlTable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;

/** HSM-based authentication related sub-command */
@Command(
    name = HSMSubCommand.COMMAND_NAME,
    description = "Generate a key stored in an HSM.",
    mixinStandardHelpOptions = true)
public class HSMSubCommand extends AccountGeneratorSubCommand {

  private static final Logger LOG = LogManager.getLogger();
  public static final String COMMAND_NAME = "hsm-account-generator";
  private HSMConfig hsmConfig;

  public HSMSubCommand() {}

  @SuppressWarnings("unused") // Picocli injects reference to command spec
  @Spec
  private CommandLine.Model.CommandSpec spec;

  @CommandLine.Option(
      names = {"-c", "--config"},
      description = "The path to a config file to initialize generator provider",
      paramLabel = MANDATORY_PATH_FORMAT_HELP,
      arity = "1",
      required = true)
  private Path config;

  @Override
  public KeyGeneratorProvider createGeneratorFactory(Path directory)
      throws KeyGeneratorInitializationException {
    Optional<TomlParseResult> result = loadConfig(config);
    if (result.isPresent()) {
      try {
        hsmConfig = getHSMConfigFrom(result.get());
        final HSMWalletProvider provider = new HSMWalletProvider(hsmConfig);
        provider.initialize();
        return new HSMKeyGeneratorFactory(provider, directory);
      } catch (final Exception e) {
        LOG.error("Unable to initialize HSM generator factory from config in " + config);
      }
    }
    return null;
  }

  @Override
  protected void validateArgs() throws KeyGeneratorInitializationException {
    checkIfRequiredOptionsAreInitialized(this);
    super.validateArgs();
  }

  @Override
  public String getCommandName() {
    return COMMAND_NAME;
  }

  public Path getConfig() {
    return config;
  }

  @Override
  public String toString() {
    if (hsmConfig != null) {
      return MoreObjects.toStringHelper(this)
          .add("config", config)
          .add("library", hsmConfig.getLibrary())
          .add("slot", hsmConfig.getSlot())
          .toString();
    }
    return "";
  }

  private static HSMConfig getHSMConfigFrom(TomlParseResult result) {
    HSMConfig.HSMConfigBuilder builder = new HSMConfig.HSMConfigBuilder();
    TomlTable hsmSignerTable = result.getTable("hsm-generator");
    if (hsmSignerTable != null && !hsmSignerTable.isEmpty()) {
      TomlTableAdapter table = new TomlTableAdapter(hsmSignerTable);
      builder.withLibrary(table.getString("library"));
      builder.withSlot(table.getString("slot"));
      builder.withPin(table.getString("pin"));
      return builder.build();
    } else {
      return builder.fromEnvironmentVariables().build();
    }
  }
}
