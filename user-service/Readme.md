### USER-SERVICE 

# Steps to Use the User-Service 

- Before Using user-service you need to install Mysql in your system. You can download Mysql through Mysql Workbench or use docker and can do the same
- If you are using docker run the following command to use mysql container. This following command will install mysql as docker container.

```
docker run -d --name mysql-server -p 3306:3306 -v mysql_data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=Welcome#123 mysql

```

- Go to application.proprties and update the MySql Root password 
- Start the springboot server 

```
 mvn spring-boot:run

 ```


 - Go to postman and write the following api(s)

```
localhost:8080/v1/vaccine/users/register

```

```

{
    "name":"Subho",
    "official_id":444,
    "address":"INDIA"
    }

```

- This Will add the particular user to the USERS TABLE 

```
localhost:8080/vaccine/users/list?official_id=<your number>

```

- This will Fetch the data based on the official id

```
localhost:8080/v1/vaccine/users/add

```

```

{
    "name":"subho",
    "date": "1999-05-12",
    "vaccine_id":233,
    "location":"INDIA",
    "officialId":444,
    "lot":4

}

```
- This will add a particular certificate in the CERTIFICATE Table

```

localhost:8080/v1/vaccine/users/retriveCertificate?officialId = <your number>

```

- This will fetch the certificate based on the particular user official id or SSN number

