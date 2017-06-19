USE [b]
GO

INSERT INTO [dbo].[Korisnik]
           ([email]
           ,[ime]
           ,[sifra])
     VALUES
           ('admin@gmail.com'
           ,'Admin'
           ,'admin')

INSERT INTO [dbo].[Preduzece]
           ([adresa]
           ,[maticniBroj]
           ,[mesto]
           ,[naziv]
           ,[pib]
           ,[tekuciRacun]
           ,[telefon])
     VALUES
           ('Nova adresa 1'
           ,'1234567896325'
           ,'Zrenjanin'
           ,'Nase preduzetje'
           ,'12345678'
           ,'123456789789456123'
           ,'021555999')

	INSERT INTO [dbo].[PoslovniPartner]
           ([adresa]
           ,[mesto]
           ,[naziv]
           ,[pib]
           ,[tekuciRacun]
           ,[telefon]
           ,[vrsta]
           ,[preduzece_id])
     VALUES
           ('Nova adresa 2'
           ,'Novi Sad'
           ,'Poslovni partner'
           ,12345678
           ,'12345678998745'
           ,'123456789'
           ,'KD'
           ,1)

	INSERT INTO [dbo].[PoslovnaGodina]
           ([aktivna]
           ,[brojGodine]
           ,[preduzece_id])
     VALUES
           ('D'
           ,'2017'
           ,1)

	INSERT INTO [dbo].[PoslovnaGodina]
           ([aktivna]
           ,[brojGodine]
           ,[preduzece_id])
     VALUES
           ('N'
           ,'2016'
           ,1)

	INSERT INTO [dbo].[Cenovnik]
           ([naziv]
           ,[datumVazenja])
     VALUES
           ('Januar'
           ,'01/15/2017')

	INSERT INTO [dbo].[Cenovnik]
           ([naziv]
           ,[datumVazenja])
     VALUES
           ('Februar'
           ,'02/14/2017')

	INSERT INTO [dbo].[Cenovnik]
           ([naziv]
           ,[datumVazenja])
     VALUES
           ('Mart'
           ,'03/15/2017')

	INSERT INTO [dbo].[Cenovnik]
           ([naziv]
           ,[datumVazenja])
     VALUES
           ('Avgust'
           ,'08/15/2017')

	INSERT INTO [dbo].[VrstaPDVa]
           ([nazivVrstePDva])
     VALUES
           ('PDV na hranu')

	INSERT INTO [dbo].[VrstaPDVa]
           ([nazivVrstePDva])
     VALUES
           ('porez na dobit preduzeca')

	INSERT INTO [dbo].[VrstaPDVa]
           ([nazivVrstePDva])
     VALUES
           ('porez na promet')

	INSERT INTO [dbo].[VrstaPDVa]
           ([nazivVrstePDva])
     VALUES
           ('porez na dodatnu vrednost')

	INSERT INTO [dbo].[VrstaPDVa]
           ([nazivVrstePDva])
     VALUES
           ('doprinosi')

	INSERT INTO [dbo].[Grupa]
           ([nazivGrupe]
           ,[preduzece_id]
           ,[vrstaPDVa_id])
     VALUES
           ('roba'
           ,1
           ,1)

	INSERT INTO [dbo].[Grupa]
           ([nazivGrupe]
           ,[preduzece_id]
           ,[vrstaPDVa_id])
     VALUES
           ('usluga'
           ,1
           ,1)

	INSERT INTO [dbo].[Podgrupa]
           ([nazivPodgrupe]
           ,[grupa_id])
     VALUES
           ('piletina'
           ,1)

	INSERT INTO [dbo].[Podgrupa]
           ([nazivPodgrupe]
           ,[grupa_id])
     VALUES
           ('svinjetina'
           ,1)

	INSERT INTO [dbo].[Podgrupa]
           ([nazivPodgrupe]
           ,[grupa_id])
     VALUES
           ('Izgradnja stambenih i nestambenih zgrada'
           ,2)

	INSERT INTO [dbo].[Podgrupa]
           ([nazivPodgrupe]
           ,[grupa_id])
     VALUES
           ('Izgradnja puteva i autoputeva'
           ,2)

	INSERT INTO [dbo].[Podgrupa]
           ([nazivPodgrupe]
           ,[grupa_id])
     VALUES
           ('Priprema gradilista'
           ,2)

	INSERT INTO [dbo].[KatalogRobeIUsluga]
           ([nazivStavkeKataloga]
           ,[opisStavkeKataloga]
           ,[podgrupa_id])
     VALUES
           ('Batak'
           ,'opis stavke'
           ,1)

	INSERT INTO [dbo].[KatalogRobeIUsluga]
           ([nazivStavkeKataloga]
           ,[opisStavkeKataloga]
           ,[podgrupa_id])
     VALUES
           ('Butkica'
           ,'opis stavke'
           ,2)

	INSERT INTO [dbo].[KatalogRobeIUsluga]
           ([nazivStavkeKataloga]
           ,[opisStavkeKataloga]
           ,[podgrupa_id])
     VALUES
           ('Noga'
           ,'opis stavke'
           ,1)

	INSERT INTO [dbo].[KatalogRobeIUsluga]
           ([nazivStavkeKataloga]
           ,[opisStavkeKataloga]
           ,[podgrupa_id])
     VALUES
           ('Prepravke'
           ,'opis stavke'
           ,4)

	INSERT INTO [dbo].[KatalogRobeIUsluga]
           ([nazivStavkeKataloga]
           ,[opisStavkeKataloga]
           ,[podgrupa_id])
     VALUES
           ('Postavljanje skele'
           ,'opis stavke'
           ,5)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (50000
           ,1
           ,1)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (10000
           ,2
           ,1)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (15000
           ,3
           ,1)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (25000
           ,4
           ,1)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (1200
           ,1
           ,2)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (7000
           ,2
           ,2)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (100
           ,3
           ,2)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (3000
           ,4
           ,2)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (500
           ,1
           ,3)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (2800
           ,2
           ,3)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (150
           ,3
           ,3)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (256
           ,4
           ,3)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (5800
           ,1
           ,4)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (10800
           ,2
           ,4)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (1790
           ,3
           ,4)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (1236
           ,4
           ,4)
	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (750
           ,1
           ,5)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (17900
           ,2
           ,5)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (1560
           ,3
           ,5)

	INSERT INTO [dbo].[StavkaCenovnika]
           ([cena]
           ,[cenovnik_id]
           ,[katalogRobeIUsluga_id])
     VALUES
           (5690
           ,4
           ,5)

	INSERT INTO [dbo].[StopaPDVa]
           ([datumKreiranja]
           ,[procenatPDVa]
           ,[vrstaPDVa_id])
     VALUES
           ('03/06/2017'
           ,10
           ,1)

	INSERT INTO [dbo].[StopaPDVa]
           ([datumKreiranja]
           ,[procenatPDVa]
           ,[vrstaPDVa_id])
     VALUES
           ('08/23/2017'
           ,20
           ,1)
GO

