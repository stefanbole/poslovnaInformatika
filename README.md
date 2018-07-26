# poslovnaInformatika
###### Fakultet tehničkih nauka, Novi Sad
###### Poslovna informatika

## Za implementaciju je korišćeno:
* [Play 1.4.4](https://www.playframework.com/download) - Web framework
* [Microsoft SQL Server](https://www.microsoft.com/en-gb/sql-server/sql-server-downloads) - Baza podataka

## Instalacija
Pre pokretanja aplikacije je potrebno:
1. instalirati play, i
2. kreirati bazu.


### Kreiranje baze podataka
Nakon kreiranja baze, u fajlu application.conf (conf/application.conf) potrebno je izmeniti sledeće linije:
```
 db.default.url=jdbc:jtds:sqlserver://localhost/**nazivBaze**
 db.default.user=**username**
 db.default.pass=**password**
 ```

## Pokretanje aplikacije
Potrebno je podesiti Java Build Path -> Libraries -> play 1.4.4. 

Pre pokretanja aplikacije potrebno je izvrsiti komandu play dependencies test.
Za pokretanje aplikacije koristiti komandu play run test, gde je **test** naziv projekta (potrebno je preći u folder gde se nalazi projekat):

```
play dependencies test
```
```
play run test
```

zatim u pretraživaču uneti:
```
http://localhost:9000/
```

ako se zeli pristupiti konkretnoj stranici, npr. stranici preduzeca, ukucati sledece:
```
http://localhost:9000/preduzeca/show
```

Za zaustavljanje aplikacije koristiti:
```
Ctrl + c
```
