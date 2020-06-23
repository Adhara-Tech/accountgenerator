###Install SoftHSM
####Linux
As a prerequisite you might need to install the following dependencies if not already present 
```
apt install gcc make automake libtool autoconf pkg-config libssl-dev -y
```
Clone the repo
```
git clone https://github.com/opendnssec/SoftHSMv2
cd SoftHSMv2/
sh autogen.sh
./configure
make
make install
```
The last command may require super user permissions in which case one should also do 
```
sudo chmod 1777 /var/lib/softhsm
```
####Mac
```
brew install softhsm
```
###Initialize SoftHSM
```
softhsm2-util --init-token --slot 0 --label WALLET-000 --pin us3rs3cur3 --so-pin sup3rs3cur3
softhsm2-util --init-token --slot 1 --label WALLET-001 --pin us3rs3cur3 --so-pin sup3rs3cur3
softhsm2-util --init-token --slot 2 --label WALLET-002 --pin us3rs3cur3 --so-pin sup3rs3cur3
softhsm2-util --init-token --slot 3 --label WALLET-003 --pin us3rs3cur3 --so-pin sup3rs3cur3
softhsm2-util --show-slots
```
###Run SoftHSM in Docker
Build the image
```
docker build --tag softhsm2:2.5.0 .
```
Run the image
```
docker run -ti --rm softhsm2:2.5.0 sh -l
```
Test 
```
softhsm2-util --show-slots
```
The docker image comes with pre-initialized slots
###Build AccountGenerator
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
###Key generation with HSM:
Start the service
```
accountgenerator --http-listen-host=127.0.0.1 --http-listen-port=8542 --logging="DEBUG" --directory="./keysAndPasswords" hsm-account-generator --library="/usr/local/lib/softhsm/libsofthsm2.so" --slot-label="WALLET-001" --slot-pin="us3rs3cur3"  
```
Generate a new account
```
curl -w "\n" -X POST --data '{"jsonrpc":"2.0","method":"eth_generateAccount","params":[],"id":1}' http://127.0.0.1:8542
```
###Key generation with Keystore:
Start the service
```
accountgenerator --http-listen-host=127.0.0.1 --http-listen-port=8543 --logging="DEBUG" --directory="./keysAndPasswords" file-based-account-generator --password-file="./keysAndPasswords/passwordFile"  
```
Generate a new account
```
curl -w "\n" -X POST --data '{"jsonrpc":"2.0","method":"eth_generateAccount","params":[],"id":1}' http://127.0.0.1:8543
```
