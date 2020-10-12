/*
 * Copyright 2019 ConsenSys AG.
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
package tech.pegasys.accountgenerator;

import tech.pegasys.accountgenerator.core.AccountGenerator;
import tech.pegasys.accountgenerator.core.InitializationException;
import tech.pegasys.accountgenerator.core.KeyGeneratorProvider;

import java.nio.file.Path;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.tuweni.toml.TomlInvalidTypeException;
import org.apache.tuweni.toml.TomlParseResult;
import picocli.CommandLine;

public abstract class AccountGeneratorSubCommand implements Runnable {

  private static final Logger LOG = LogManager.getLogger();

  @CommandLine.ParentCommand private AccountGeneratorBaseCommand config;

  public abstract KeyGeneratorProvider createGeneratorFactory(Path directory)
      throws KeyGeneratorInitializationException;

  public abstract String getCommandName();

  protected void validateArgs() throws InitializationException {
    if (config != null) {
      config.validateArgs();
    }
  }

  @Override
  public void run() throws KeyGeneratorInitializationException {

    validateArgs();

    // set log level per CLI flags
    System.out.println("Setting logging level to " + config.getLogLevel().name());
    Configurator.setAllLevels("", config.getLogLevel());

    LOG.debug("Configuration = {}", this);
    LOG.info("Version = {}", ApplicationInfo.version());

    final AccountGenerator generator =
        new AccountGenerator(config, createGeneratorFactory(config.getDirectory()));
    generator.run();
  }

  protected static Optional<TomlParseResult> loadConfig(Path file) {
    if (file == null) {
      return Optional.empty();
    } else {
      String filename = file.getFileName().toString();

      try {
        return Optional.of(
            TomlConfigFileParser.loadConfigurationFromFile(file.toAbsolutePath().toString()));
      } catch (TomlInvalidTypeException | IllegalArgumentException var4) {
        String errorMsg = String.format("%s failed to decode: %s", filename, var4.getMessage());
        LOG.error(errorMsg);
        return Optional.empty();
      } catch (Exception var5) {
        LOG.error("Could not load TOML file " + file, var5);
        return Optional.empty();
      }
    }
  }
}
