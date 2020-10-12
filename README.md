### Build AccountGenerator
```
./gradlew build
```
Go to the distribution directory:
```
cd build/distributions/
```
Expand the distribution archive:
```
tar -xzf accountgenerator-<version>.tar.gz
```
Move to the expanded folder and display the AccountGenerator help to confirm installation.
```
cd accountgenerator-<version>/
bin/accountgenerator --help
```
### Key generation with Keystore:
Start the service
```
accountgenerator --http-listen-host=127.0.0.1 --http-listen-port=4545 --logging="INFO" --directory="../keysAndPasswords" file-based-account-generator --password-file="../keysAndPasswords/passwordFile"  
```
Generate a new account
```
curl -w "\n" -X POST --data '{"jsonrpc":"2.0","method":"eth_generateAccount","params":[],"id":1}' http://127.0.0.1:7545 | jq
```
### Key generation with HSM:
Start the service
```
accountgenerator --http-listen-host=127.0.0.1 --http-listen-port=7545 --logging="INFO" --directory="../keysAndPasswords" hsm-account-generator --config="../accountgenerator-config-softhsm.toml"  
```
Generate a new account
```
curl -w "\n" -X POST --data '{"jsonrpc":"2.0","method":"eth_generateAccount","params":[],"id":1}' http://127.0.0.1:7545 | jq
```
### Key generation with Cavium:
Start the service
```
accountgenerator --http-listen-host=127.0.0.1 --http-listen-port=7543 --logging="INFO" --directory="../keysAndPasswords" cavium-account-generator --config="../accountgenerator-config-cavium.toml"
```
Generate a new account
```
curl -w "\n" -X POST --data '{"jsonrpc":"2.0","method":"eth_generateAccount","params":[],"id":1}' http://127.0.0.1:7545 | jq
```


