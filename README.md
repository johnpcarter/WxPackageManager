## WxPackageManager

webMethods package to provide an online central registry for looking up packages and obtaining the necessary git repository info in order to download the package locally.

The package provides a portal to allow packages to be browsed, registered and downloaded here

http://localhost:5555/WxPackageManager

The package also provides an API to implement remote use for third party client tools such as the webMethods wpm cli command.

e.g.

List available packages (in default registry)
```
$ curl "http://localhost:5555/rad/wx.packages:manager/packages?registry=default" \
 -H 'Accept: application/json'
 {"packages":[{"name":"JcPublicTools","category":"tools","description":"Bunch of utility services for general use when building webMethods Integrations","registeredDate":"24/01/2022","searchTags":["\"utility\"","\"general\""],"totalDownloads":"0"}]}
 ```
 
* Get download information for a package
 ```
 curl "http://localhost:5555/rad/wx.packages:manager/package/JcPublicTools" \
  -H 'Accept: application/json'
 {"sourceUrl":"https://github.com/johnpcarter/JcPublicTools.git","sourceUserId":null,"sourceToken":null,"trustLevel":null,"isSigned":false,"reason":null,"isValid":false}         
 ```
 
Full API documentation is available here online 
 
http://localhost:5555/WxPackageManager/api

Usage guide here

http://localhost:5555/WxPackageManager/help
 
## Set up
Install the package (WxPackageManager) into your webMethods Integration Server, along with the dependent 
package JcPublicTools
e.g.
 
```
 $ cd ${SAG_HOME}/IntegrationServer/instances/default/packages
 $ git clone https://github.com/johnpcarter/JcPublicTools.git
 $ git clone https://github.com/johnpcarter/WxPackageManager.git
```
 
 Then restart your server.
 
# Key/Trust stores

The package registry leverages a dedicated key store for generating JWT tokens to allow remote access.
You will need to create your own key and trust store beforehand as for security reasons these files cannot provided by us.

### Create the key and trust store files

Create a private/public key pair
```
$ keytool -genkey -alias packages.softwareag.com -keyalg RSA -keystore wm-packages-manager-keystore.jks -keysize 1024
```

Create a certificate request
```
$ keytool -certreq -alias packages.softwareag.com -file packages.softwareag.csr -keystore wm-packages-manager-keystore.jks -storepass mypwd
```

Send the output from the following command to your certificate authority
```
$ keytool -printcertreq -file packages.softwareag.csr
```

Now import the certificate into your key store
```
$ keytool -import -trustcacerts -keystore wm-packages-manager-keystore.jks -storepass mypwd -alias packages.softwareag.com -file wm-packages.com.cer
```

The trust store should reference the CA that signed your private key e.g.
```
$ keytool -import -alias packages.softwareag.com -keystore wm-packages-manager-truststore.jks -file go-daddy-ca-root.crt -deststoretype jks
```

### Import the files into your IS/MSR.

After creating your key and trust store files, proceed to your admin portal and navigate to Security -> Keystore

Click on "Create keystore Alias", Specify 'wm_packages_manager' as the alias name and enter the location and password word to your files.
Now import the trust store by clicking on "Create truststore alias", use the same alias name i.e. "wm_packages_manager".

Alternatively, you can configure an MSR using configuration variables e.f.
```
keystore.wm_packages_manager.ksLocation=./packages/WxPackageManager/config/wm-packages-manager-keystore.jks
keystore.wm_packages_manager.ksPassword=*****
keystore.wm_packages_manager.keyAlias.packages.softwareag.com.keyAliasPassword=*****
truststore.wm_packages_manager.ksLocation=./packages/WxPackageManager/config/wm-packages-manager-truststore.jks
truststore.wm_packages_manager.ksPassword=*****
```

### Using the default key store

You can also choose to use the default key and trust store provided by your IS/MSR by adding a global variable
named 'com.wx.packagemanager.usedefaultkeystore' and set the value to true. However, your security will be 
compromised if you have never replaced the defaults provided by Software AG.

# Configuring JWT authentication

To allow JWT token authentication, configure your JWT settings via Security -> JWT in the IS/MSR admin portal

Click on 'Trusted issuers' and then 'Add Issuer', specify the value 'SoftwareAG'
Then click on 'Return to JWT', followed by 'Issuer Configuration'. On this page click 'Add Issuer Certificate Mapping'.

Choose the issuer 'SoftwareAG' and then either the trust store alias 'wm_packages_manager' or 'DEFAULT_IS_TRUSTSTORE'
depennding on the configuration you chose in the previous section. The certificate alias will be whatever is presented
in the drop down.

# Database setup
 
You will need to provide a mysql database and the configure the connection parameters in your webMethods runtime. You can do this after starting up your server via the adapters -> jdbc adapter -> connections -> 'wx.packages.manager._priv.jdbc:conn'

Alternatively you can copy the connection properties file application.properties in the packages WxPackageManager resources directory to the root folder or your Integration Server/MSR and edit the connection properties to point to your database before starting up the server.
 
First, create the database and default user e.g.
```
 CREATE DATABASE wxpm;
 USE wxpm;

 CREATE USER 'wxpm'@'%' IDENTIFIED BY 'manage';
 GRANT ALL PRIVILEGES ON *.* TO 'wxpm'@'%';
 GRANT SUPER ON *.* TO 'wxpm'@'%';
  ```
 
 You will have to reload the package after setting the database connection if the server is already started. The necessary database tables will then be created dynamically when the package starts. A default public registry is also created automatically.
 