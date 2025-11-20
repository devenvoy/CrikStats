```shell
cd ./app/release
```

## Recreate .apks with connected-device mode
```shell
java -jar bundletool.jar build-apks `
  --bundle=app-release.aab `
  --output=app.apks `
  --connected-device `
  --ks="D:\Test\CrikStats\com.devansh.crikstats.jks" `
  --ks-pass=pass:com.devansh.crikstats `
  --ks-key-alias=com.devansh.crikstats `
  --key-pass=pass:com.devansh.crikstats
```

## install everything cleanly
```shell
java -jar bundletool.jar install-apks --apks=app.apks
```
## Check installed splits
- mac/linux
```shell
adb shell pm list packages -f | grep crikstats
```
- win 
```shell
adb shell pm list packages -f | Select-String crikstats
```
```shell
adb shell pm path com.devansh.crikstats
```

## uninstall app
```shell
 adb uninstall com.devansh.crikstats
```