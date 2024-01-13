![](repo/logo.png)

# Krypton-Server

The backend of the Krypton password manager

___


### Development notes
#### Rebuild project
If you clone the project, or change the spec.yaml always rebuild the project.
Also, if the spec.yaml cannot be found, clean-rebuild the project.
#### Default OpenAPI Password
The default credentials for the swagger documentation in the development environment are "demo:demo"

### Deployment notes

| Required Environment Variable |
|-------------------------------|
| KRYPTON_DB_HOST               |
| KRYPTON_DB_PORT               |
| KRYPTON_DB_NAME               |
| KRYPTON_DB_USER               |
| KRYPTON_DB_PASSWORD           |
