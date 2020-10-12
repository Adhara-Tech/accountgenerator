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

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;

public class CaviumConfig {

  // The HSM PKCS11 library used to generate accounts.
  private final String library;
  // The crypto user pin of the HSM slot used to generate accounts.
  private final String pin;
  // The script used to set attributes.
  private final String sas;

  @JsonCreator
  public CaviumConfig(final String library, final String pin, final String sas) {
    this.library = library;
    this.pin = pin;
    this.sas = sas;
  }

  public CaviumConfig() {
    this.library = "";
    this.pin = "";
    this.sas = "";
  }

  public String getLibrary() {
    return library;
  }

  public String getPin() {
    return pin;
  }

  public String getSas() {
    return sas;
  }

  public static class CaviumConfigBuilder {

    private String library;
    private String pin;
    private String sas;

    public CaviumConfigBuilder withLibrary(final String library) {
      this.library = library;
      return this;
    }

    public CaviumConfigBuilder withPin(final String pin) {
      this.pin = pin;
      return this;
    }

    public CaviumConfigBuilder withSas(final String sas) {
      this.sas = sas;
      return this;
    }

    public CaviumConfigBuilder fromEnvironmentVariables() {
      library = System.getenv("AWS_HSM_LIB");
      pin = System.getenv("AWS_HSM_PIN");
      sas = System.getenv("AWS_HSM_SAS");
      return this;
    }

    public CaviumConfig build() {
      checkNotNull(library, "AWS Cloud HSM library was not set.");
      checkNotNull(pin, "AWS Cloud HSM pin was not set.");
      checkNotNull(sas, "AWS Cloud HSM sas was not set.");
      return new CaviumConfig(library, pin, sas);
    }
  }
}
