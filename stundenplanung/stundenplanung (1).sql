-- phpMyAdmin SQL Dump
-- version 5.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 17. Dez 2020 um 15:10
-- Server-Version: 10.4.11-MariaDB
-- PHP-Version: 7.4.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `stundenplanung`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `accounts`
--

CREATE TABLE `accounts` (
  `AccID` int(6) NOT NULL,
  `AccName` varchar(50) NOT NULL,
  `AccPwd` varchar(20) NOT NULL,
  `AccEmail` varchar(100) NOT NULL,
  `FK_GroupID` tinyint(4) DEFAULT 50,
  `FK_FBID` int(5) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `accounts`
--

INSERT INTO `accounts` (`AccID`, `AccName`, `AccPwd`, `AccEmail`, `FK_GroupID`, `FK_FBID`) VALUES
(47, 'User1', '1234', 'user1@fh-bielefeld.de', 2, 9),
(48, 'User2', '1234', 'user2@fh-bielefeld.de', 2, 8),
(49, 'User3', '1234', 'user3@fh-bielefeld.de', NULL, 7),
(50, 'User4', '1234', 'user4@fh-bielefeld.de', NULL, 1),
(51, 'User5', '1234', 'user5@fh-bielefeld.de', NULL, 3),
(52, 'User6', '1234', 'user6@fh-bielefeld.de', NULL, 2),
(53, 'User7', '1234', 'user7@fh-bielefeld.de', NULL, 3),
(54, 'User8', '1234', 'user8@fh-bielefeld.de', NULL, 1),
(55, 'User9', '1234', 'user9@fh-bielefeld.de', 2, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benutzergruppe`
--

CREATE TABLE `benutzergruppe` (
  `GroupID` tinyint(4) NOT NULL,
  `BGName` varchar(50) NOT NULL,
  `BGShortName` varchar(5) NOT NULL,
  `BGRechte` tinyint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `benutzergruppe`
--

INSERT INTO `benutzergruppe` (`GroupID`, `BGName`, `BGShortName`, `BGRechte`) VALUES
(1, 'Administrator*in', 'ADM', 96),
(2, 'Nobody', 'NOB', 32),
(3, 'Studiengangsleiter*in', 'SGL', 64),
(9, 'RaumZeitPlaner*in', 'RZP', 16);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `dozenten`
--

CREATE TABLE `dozenten` (
  `DID` int(5) NOT NULL,
  `DName` varchar(60) NOT NULL,
  `DVorname` varchar(60) NOT NULL,
  `DTitel` varchar(40) NOT NULL,
  `DKurz` varchar(6) NOT NULL,
  `FK_AccID` int(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `dozenten`
--

INSERT INTO `dozenten` (`DID`, `DName`, `DVorname`, `DTitel`, `DKurz`, `FK_AccID`) VALUES
(2, 'Bachmann', 'Bernhard', 'Prof. Dr. phil.', 'Bm', NULL),
(4, 'Biegler-König', 'Cornelia', 'OStR.in', 'Bk', NULL),
(5, 'Biegler-König', 'Friedrich', 'Prof. Dr. math.', 'Bi', NULL),
(10, 'Cottin', 'Claudia', 'Prof\'n. Dr. rer. nat.', 'Co', NULL),
(11, 'Diekmann', 'Paul', 'Prof. Dr.-Ing.', 'Di', NULL),
(13, 'Dornseifer-Seitz', 'Adelheid', 'Dipl. Übersetzerin', 'Ds', NULL),
(14, 'Draxl', 'Sybille', 'Dipl. math.', 'Dx', NULL),
(15, 'Dürkopp', 'Klaus', 'Prof. Dr.-Ing.', 'Dü', NULL),
(16, 'Feyerabend', 'Franz', 'Prof. Dr.-Ing.', 'Fb', NULL),
(18, 'Grünwoldt', 'Lutz', 'Prof. Dr.-Ing.', 'Gr', NULL),
(19, 'Gudermann', 'Frank', 'Prof. Dr. rer. nat.', 'Gm', NULL),
(21, 'Haubrock', 'Jens', 'Prof. Dr.-Ing.', 'Ha', NULL),
(22, 'Schöning', 'Sonja', 'Prof. Dr. rer. nat.', 'Sh', NULL),
(24, 'Hofer', 'Klaus', 'Prof. Dr. Ing. habil.', 'Hf', NULL),
(25, 'Hoffmann', 'Sebastian', 'Prof. Dr.-Ing.', 'Hm', NULL),
(26, 'Hüsgen', 'Bruno', 'Prof. Dr.-Ing.', 'Hü', NULL),
(28, 'Jaroschek', 'Christoph', 'Prof. Dr.-Ing.', 'Jk', NULL),
(30, 'Kaschuba', 'Reinhard', 'Prof. Dr.-Ing.', 'Kb', NULL),
(32, 'Kisse', 'Raimund', 'Prof. Dr.-Ing.', 'Ki', NULL),
(36, 'Kühlert', 'Heinrich', 'Prof. Dr.-Ing.', 'Kü', NULL),
(37, 'Lajios', 'Georgios', 'Prof. Dr. rer. nat.', 'La', NULL),
(38, 'Loviscach', 'Jörn', 'Prof. Dr. rer. nat.', 'Lo', NULL),
(39, 'Lütkemeyer', 'Dirk', 'Prof. Dr. rer. nat.', 'Lü', NULL),
(40, 'Manz-Schumacher', 'Hildegard', 'Prof. Dr. rer. pol.', 'Ma', NULL),
(41, 'Naumann', 'Rolf', 'Prof. Dr.-Ing.', 'Na', NULL),
(43, 'Ohlhoff', 'Antje', 'Prof. Dr. rer. nat.', 'Oh', NULL),
(44, 'Panreck', 'Klaus', 'Prof. Dr.-Ing.', 'Pa', NULL),
(45, 'Patel', 'Anant', 'Prof. Dr. rer. nat.', 'Pa', NULL),
(46, 'Petrova', 'Svetozara', 'Prof. Dr. rer. nat.', 'Pt', NULL),
(47, 'Petry', 'Martin', 'Prof. Dr. rer. nat.', 'Pe', NULL),
(49, 'Schierenberg', 'Marc-Oliver', 'Prof. Dr. rer. nat.', 'Si', NULL),
(51, 'Schmidt', 'Norbert', 'Prof. Dr.-Ing.', 'Sd', NULL),
(53, 'Schröder', 'Christian', 'Prof. Dr. rer. nat.', 'Sö', NULL),
(54, 'Schultheis', 'Rüdiger', 'Prof. Dr.-Ing.', 'Sl', NULL),
(55, 'Schumacher', 'Bernd-Josef', 'Prof. Dr. rer. nat.', 'Sc', NULL),
(56, 'Schwenzfeier-Hellkamp', 'Eva', 'Prof. Dr.-Ing.', 'Sp', NULL),
(58, 'Ueckerdt', 'Rainer', 'Prof. Dr. sc. techn. Dr. rer. nat.', 'Ue', NULL),
(59, 'Vucetic', 'Dragan', 'Prof. Dr.', 'Vu', NULL),
(60, 'Wameling', 'Hubertus', 'Prof. Dr.', 'Wm', NULL),
(61, 'Waßmuth', 'Joachim', 'Prof. Dr.-Ing.', 'Wu', NULL),
(63, 'Weidemann', 'Dirk', 'Prof. Dr.-Ing.', 'Wn', NULL),
(65, 'Zielke', 'Dirk', 'Prof. Dr.-Ing.', 'Zi', NULL),
(73, 'Liebing', 'Günter', 'Dr.', 'Li', NULL),
(74, 'Niemann', 'Martin', 'Dr.', 'Ni', NULL),
(75, 'Jakobs-Schönwandt', 'Desiree', 'Dr.', 'Js', NULL),
(78, 'Schneider', 'Axel', 'Prof. Dr. rer. nat.', 'SrA', NULL),
(81, 'Feldmann', 'Dirk', 'Dr. rer. nat.', 'Fe', NULL),
(82, 'Thole', 'Frank', 'Dipl.-Ing.', 'Th', NULL),
(86, 'Fromme', 'Lars', 'Prof. Dr. rer.nat.', 'Fr', NULL),
(92, 'Vemmer', 'Marina', 'M.Sc.', 'Ve', NULL),
(95, 'Böttner', 'Rudolph', 'Dr.', 'Bö', NULL),
(96, 'Westerwalbesloh', 'Thomas', 'Prof. Dr.', 'Ww', NULL),
(98, 'Komarnicki', 'Przemyslaw', 'Dr.', 'Ko', NULL),
(99, 'Viertel', 'Klaus', '', 'Vi', NULL),
(100, 'Homburg', 'Sarah Vanessa', 'M.Sc.', 'Hg', NULL),
(101, 'Sauser', '', 'Prof. Dr.-Ing.', 'Sr', NULL),
(102, 'Schenck', 'Wolfram', 'Prof. Dr.', 'Sk', NULL),
(103, 'Stockem', 'Irina', 'ME', 'So', NULL),
(106, 'Kohlhase', 'Martin', 'Prof. Dr.-Ing.', 'Kh', NULL),
(107, 'Lorenz', 'Sissy-Christin', 'M.Sc.', 'Lr', NULL),
(108, 'Prott-Warner', 'Vanessa', 'DI', 'Pw', NULL),
(110, 'Gronemann', 'Andreas', 'Dipl.-Ing. (FH)', 'Gn', NULL),
(111, 'Teich', 'Lisa', 'M.sc.', 'Te', NULL),
(112, 'Stötzer', 'Martin', 'Dr.', 'St', NULL),
(114, 'Bekemeier', 'Simon', 'M.Eng.', 'Be', NULL),
(115, 'Krell', 'Vivien', 'M.Sc.', 'Kr', NULL),
(116, 'Lemke', 'Annika Vera', 'M.Sc.', 'Lk', NULL),
(118, 'Hesse', 'Thomas', 'Prof. Dr.-Ing.', 'He', NULL),
(119, 'Hilbig', 'Thomas', 'Dipl.-Ing.', 'Hi', NULL),
(120, 'Charles', 'Peter', 'Prof. Dr.-Ing.', 'Cs', NULL),
(121, 'Horst', 'Jörg', 'Dr.rer.nat.', 'Ht', NULL),
(122, 'Schlüter', 'Ralph', '', 'SL', NULL),
(123, 'Humbert', '', '', 'Hu', NULL),
(124, 'Schoden', 'Fabian', 'WMA', 'So', NULL),
(125, 'Wrede', 'Nicola', '', 'Wr', NULL),
(126, 'Bünte', 'Andreas', 'Dr.-Ing.', 'Bü', NULL),
(127, 'Hartman', 'Holger', 'Dipl.-VW', 'Hm', NULL),
(128, 'Wasmuth', 'Joachim', 'Dr.-Ing.', 'Wa', NULL),
(129, 'Herrmann', 'Katharina', '', 'Hr', NULL),
(130, 'Preiser', '???', 'Dr.', 'Ps', NULL),
(131, 'Lösenbeck', 'Hr.', 'DI', 'Ls', NULL),
(132, 'Horstmann', 'Magnus', 'Prof. Dr.-Ing.', 'Hn', NULL),
(133, 'Fahrig', 'Michael', 'Prof. Dr.-Ing.', 'Fa', NULL),
(134, 'Funke', 'Herbert', 'Prof. Dr.-Ing.', 'Fu', NULL),
(135, 'Kaimann', 'Andrea', 'Prof\'n. Dr.-Ing.', 'Km', NULL),
(136, 'Manowicz', 'Adam-Alexander', 'Prof. Dr.', 'Mw', NULL),
(141, 'Lüke', 'Sabine', 'M.Sc.', 'Lk', NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `faculty`
--

CREATE TABLE `faculty` (
  `FBID` int(5) NOT NULL,
  `FacName` varchar(256) COLLATE utf16_german2_ci NOT NULL,
  `FacShortName` varchar(20) COLLATE utf16_german2_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_german2_ci;

--
-- Daten für Tabelle `faculty`
--

INSERT INTO `faculty` (`FBID`, `FacName`, `FacShortName`) VALUES
(1, 'Alle', 'ALL'),
(2, 'Wirtschaft', 'WI'),
(3, 'Campus Minden', 'CMI'),
(4, 'Sozialwesen', 'SOW'),
(5, 'Gestaltung', 'GES'),
(6, 'Ingenieurwissenschaften und Mathematik (Gütersloh)', 'IUMG'),
(7, 'Pflege und Gesundheit', 'PUG'),
(8, 'Ingenieurwissenschaften und Mathematik (VERBUND)(Bielefeld)', 'IUMB_Verbund'),
(9, 'Ingenieurwissenschaften und Mathematik (ALLGEMEIN)(Bielefeld)', 'IUMB_Allg');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `lehrveranstaltungsart`
--

CREATE TABLE `lehrveranstaltungsart` (
  `LVID` int(11) NOT NULL,
  `LVNAME` varchar(255) NOT NULL,
  `LVDAUER` enum('45','90','180') NOT NULL DEFAULT '90',
  `LVKURZ` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `lehrveranstaltungsart`
--

INSERT INTO `lehrveranstaltungsart` (`LVID`, `LVNAME`, `LVDAUER`, `LVKURZ`) VALUES
(1, 'Vorlesung', '90', 'V'),
(2, 'Seminar', '45', 'S'),
(3, 'Praktikum_90', '90', 'P90'),
(4, 'Praktikum_180', '180', 'P180');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `location`
--

CREATE TABLE `location` (
  `LID` int(5) NOT NULL,
  `LCity` varchar(100) COLLATE utf16_german2_ci NOT NULL,
  `LStreet` varchar(100) COLLATE utf16_german2_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_german2_ci;

--
-- Daten für Tabelle `location`
--

INSERT INTO `location` (`LID`, `LCity`, `LStreet`) VALUES
(1, 'Bielefeld', 'Interaktion 1'),
(2, 'Bielefeld', 'Lampingstraße 3'),
(3, 'Gütersloh', 'Langer Weg 9 a (Gleis 13)'),
(4, 'Gütersloh', 'Schulstraße');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `modul`
--

CREATE TABLE `modul` (
  `ModID` int(11) NOT NULL,
  `ModName` varchar(50) NOT NULL,
  `ModKuerzel` varchar(50) NOT NULL,
  `PCID` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `modul`
--

INSERT INTO `modul` (`ModID`, `ModName`, `ModKuerzel`, `PCID`) VALUES
(1, 'Antriebssysteme', 'ATS', 22),
(2, 'Elektrische Netze', 'EV1', 48),
(3, 'Dezentrale Energiesysteme (RGE)', 'DEZ', 126),
(4, 'Antriebstechnik (ET)', 'ATR', 43),
(5, 'Automatisierung von Energiesystemen', 'ATE', 86),
(6, 'Automatisierungstechnik (INI)', 'AT', 7),
(8, 'Anlagenplanung', 'EV2', 127),
(9, 'Elektrische Maschinen', 'EM', 12),
(10, 'Elektromagnetische Verträglichkeit', 'EMV', 22),
(11, 'Elektronik I (RGE)', 'EL1', 2),
(12, 'Elektronik II (RGE)', 'EL2', 3),
(13, 'Elektrotraktion', 'ETR', 68),
(15, 'Energiewirtschaft', 'EW', 21),
(18, 'Signale und Systeme', 'SIS', 123),
(19, 'Leistungselektronik', 'LE', 12),
(20, 'Mess- und Prüfsysteme', 'MPS', 116),
(21, 'Messtechnik', 'MT', 41),
(22, 'Mikrosystemtechnik', 'MST', 59),
(24, 'Photovoltaik', 'PHV', 120),
(25, 'Regelungstechnik', 'RT', 42),
(26, 'Sensorik', 'SEN', 47),
(27, 'Technical English I', 'FSE1', 6),
(28, 'Technical English II', 'FSE2', 112),
(29, 'Thermische Nutzung regenerativer Energien', 'TNE', 12),
(30, 'Thermische Nutzung regenerativer Energien', 'TNE', 118),
(31, 'Werkstoffe der Elektrotechnik und Elektronik', 'WEE', 1),
(54, 'Datenbankanwendungen', 'DBA', 59),
(55, 'Simulationstechnik', 'SIM', 61),
(57, 'High Performance Computing (PVS)', 'HPC', 60),
(62, 'Softwareengineering', 'SWE', 21),
(71, 'Digitalelektronik II', 'DEL2', 1),
(75, 'Numerische Mathematik', 'NM', 9),
(76, 'Numerische Simulation', 'NSI', 10),
(78, 'Digitalelektronik I', 'DEL1', 4),
(79, 'Hochfrequenzelektronik', 'HFE', 126),
(81, 'Netzwerktechnik', 'NW', 41),
(84, 'Mikrocontroller', 'MC', 112),
(88, 'Biogas und Bioraffinerien', 'BIO', 138),
(89, 'Anlagenplanung', 'APL', 127),
(94, 'Biochemie und Mikrobiologie', 'BCM', 2),
(95, 'Chemie', 'CH', 29),
(98, 'Elektronik', 'ELR', 34),
(99, 'Elektrotechnische Grundlagen', 'GE', 29),
(103, 'Informatik I (RGE)', 'INF1', 4),
(104, 'Informatik II (RGE)', 'INF2', 7),
(107, 'Mathematik I', 'MA1', 27),
(108, 'Mathematik II', 'MA2', 36),
(109, 'Mechanische Verfahrenstechnik', 'MVT', 3),
(112, 'Physik I', 'PHY1', 28),
(113, 'Physik II', 'PHY2', 3),
(115, 'Regenerative Energiewirtschaft', 'RW', 70),
(120, 'Verfahrenstechnik', 'VT', 9),
(121, 'Energieeffizienz im Gebäude', 'EIG', 96),
(127, 'Windkraftanlagen', 'WKA', 95),
(130, 'Physik I', 'PHY1', 28),
(131, 'Physik II', 'PHY2', 36),
(132, 'Informatik I (ET)', 'INF1', 31),
(133, 'Informatik II (ET)', 'INF2', 34),
(134, 'Elektrotechnik I (ET)', 'ET1', 29),
(135, 'Elektrotechnik II (ET)', 'ET2', 33),
(136, 'Elektronik I (ET)', 'EL1', 32),
(137, 'Elektronik II (ET)', 'EL2', 5),
(138, 'Mathematik I', 'MA1', 27),
(139, 'Mathematik II', 'MA2', 32),
(142, 'Physik I', 'PHY1', 28),
(143, 'Physik II', 'PHY2', 33),
(144, 'Algorithmen und Datenstrukturen', 'AUD', 32),
(145, 'Rechnerarchitekturen', 'RA', 47),
(148, 'Mikrocontroller', 'MC', 24),
(150, 'Betriebswirtschaftslehre', 'BW', 3),
(151, 'Investition und Finanzierung', 'FIN', 47),
(152, 'Investition und Finanzierung', 'FIN', 47),
(154, 'Betriebswirtschaftslehre ', 'BW', 3),
(158, 'Antriebstechnik (RGE)', 'ATR', 43),
(159, 'Automatisierungstechnik (ET)', 'AT', 7),
(160, 'Automatisierungstechnik (RGE)', 'AT', 8),
(166, 'Elektrotechnik I (RGE)', 'ET1', 1),
(167, 'Elektrotechnik II (RGE)', 'ET2', 36),
(168, 'Betriebswirtschaftslehre', 'BWL', 3),
(169, 'Mathematik III', 'MA3', 6),
(170, 'Projekt 1', 'PR', 49),
(171, 'Optoelektronik', 'OPEL', 10),
(173, 'Technisches Englisch II', 'FSE 2', 48),
(174, 'Mikrocontroller und Anwendungen', 'MIC', 27),
(175, 'Sensorsysteme', 'SES', 29),
(176, 'Elektrische Maschinen', 'EM', 62),
(177, 'Sensorik', 'SEN', 7),
(178, 'Elektrisches Powermanagement', 'EPM', 70),
(179, 'Theoretische Elektrotechnik', 'TET', 1),
(180, 'Messsysteme', 'MSS', 31),
(182, 'Nichtlineare Regelung', 'NLR', 34),
(183, 'Weitverkehrsnetze und IT-Sicherheit', 'WIS', 2),
(185, 'Mensch-Maschine Interaktion', 'MMI', 29),
(186, 'Effiziente Energiesysteme', 'EES', 34),
(187, 'Smart Grids', 'SG', 2),
(188, 'Optoelektronik', 'OPEL', 64),
(189, 'Technisches Englisch 1', 'FSE1', 5),
(190, 'Technical English I', 'FSE 1', 42),
(192, 'Produkt-Risikomanagement', 'PRM', 112),
(194, 'Thermische Verfahrenstechnik', 'TVT', 43),
(196, 'Grundlagen der Energietechnik', 'GET', 7),
(197, 'Einführung in die elektrische Energietechnik', 'EN', 8),
(198, 'Technisches English II', 'FSE-2', 48),
(200, 'Energietechnik', 'EN', 9),
(201, 'Elektrische Energiespeicher und Brennstoffzellen', 'EEB', 48),
(202, 'Bildverarbeitung und Mustererkennung', 'BVM', 15),
(204, 'Embedded Systems', 'ESYS', 69),
(205, 'Embedded Systems', 'ESYS', 69),
(207, 'Zustandsregelungen', 'ZRG', 13),
(208, 'Wind- und Wasserkraft', 'WEA', 13),
(209, 'Wind- und Wasserkraft', 'WWK', 70),
(210, 'Moderne Energiepolitik', 'MEP', 18),
(212, 'Zustandsregelungen', 'ZRG', 56),
(214, 'Effiziente Lichttechnik', 'ELT', 96),
(216, 'Gebäudeautomation', 'GAT', 49),
(217, 'Managementkompetenzen', 'MMK', 32),
(218, 'Technische Nutzung regenerativer Energien I', 'RN1', 22),
(219, 'Klima und Ressourcen', 'KLI', NULL),
(221, 'Regelungstechnik (ET)', 'RT', NULL),
(222, 'Regelungstechnik (RGE)', 'RT', 96),
(223, 'Allgemeine Didaktik & Orientierungspraktikum', 'ADO', 96),
(224, 'Mathematik I', 'MA1', 27),
(225, 'Mathematik II', 'MA2', 2),
(226, 'Berufsfeld des Elektroingenieurs', 'BER', 70),
(227, 'Projekt2', 'PR2', 70),
(228, 'Simulationstechnik', 'SIM', 13),
(229, 'Anlagenplanung', 'APL', 58),
(230, 'Elektrische Netze', 'EN', 96),
(231, 'Grundlagen der Energietechnik', 'GET', NULL),
(232, 'Wind- und Wasserkraft', 'WWK', NULL),
(233, 'Intelligente Energiesysteme', 'IES', 27),
(234, 'Mathematik A', 'MAA', 96),
(235, 'Mathematik B', 'MAB', 96),
(236, 'Mathematik C', 'MAC', 96),
(237, 'Kerntechnik', 'KT', NULL),
(250, 'Informatik I (INI)', 'INF1', NULL),
(251, 'Informatik II', 'INF2', NULL),
(252, 'Zirkuläre Wertschöpfung nach Cradle to Cradle', 'ZW', NULL),
(257, 'Betriebssysteme', 'BS', 96),
(258, 'Maschinelles Lernen und Data Mining', 'MLDM', NULL),
(259, 'Numerik für ET-Ingenieure', 'NFE', 122),
(262, 'Modul Zirkuläre Wertschöpfung nCC', 'MZW', 96),
(264, 'Einführung Ingenieurinformatik', 'EII', NULL),
(272, 'Bio-inspirierte Aktuatorik', 'FH-BMK-2040', 4),
(274, 'Embedded Systems', 'ESYS', 69),
(275, 'Angewandte Informatik', 'AINF', 16),
(276, 'Mathematik I', 'MAT1', 15),
(277, 'Physik I', 'PH1', 16),
(282, 'te', 't', 16);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `raum`
--

CREATE TABLE `raum` (
  `RID` int(5) NOT NULL,
  `RName` varchar(20) NOT NULL,
  `Kapazitaet` int(3) NOT NULL,
  `NachbarRaum` varchar(20) DEFAULT NULL,
  `FK_LID` int(5) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `raum`
--

INSERT INTO `raum` (`RID`, `RName`, `Kapazitaet`, `NachbarRaum`, `FK_LID`) VALUES
(24, 'C2', 60, 'C3', 1),
(25, 'AudiMax', 475, '', 1),
(26, 'Z4', 50, '', 3),
(27, 'G3', 35, 'G4', 3),
(28, 'G4', 25, 'G3', 3),
(29, 'A2', 60, '', 4),
(30, 'C3', 60, 'C4', 1),
(31, 'C4', 90, 'C3', 1),
(34, 'te', 30, 'test1', 4);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `sgmodul`
--

CREATE TABLE `sgmodul` (
  `SGMID` int(11) NOT NULL,
  `FK_ModID` int(11) NOT NULL,
  `FK_SGID` int(5) DEFAULT NULL,
  `FK_DID` int(5) DEFAULT NULL,
  `ModSemester` int(1) NOT NULL,
  `SGMNotiz` varchar(256) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `sgmodul`
--

INSERT INTO `sgmodul` (`SGMID`, `FK_ModID`, `FK_SGID`, `FK_DID`, `ModSemester`, `SGMNotiz`) VALUES
(47, 6, 1, 106, 3, ''),
(48, 25, 1, 106, 4, ''),
(49, 54, 1, 18, 6, 'Semesterprojekt'),
(54, 75, 1, 86, 4, ''),
(55, 76, 1, 53, 5, ''),
(56, 79, 2, 54, 5, ''),
(57, 78, 1, 118, 3, ''),
(59, 81, 1, 18, 3, ''),
(62, 134, 2, 54, 1, ''),
(63, 135, 2, 54, 2, ''),
(64, 107, 1, 43, 1, ''),
(65, 108, 1, 43, 2, ''),
(66, 112, 1, 86, 1, 'Gemeinsam mit Phy1 (ET).'),
(67, 113, 1, 86, 2, 'Gemeinsam mit Phy2 (ET).'),
(68, 132, 2, 18, 1, ''),
(69, 133, 2, 18, 2, ''),
(70, 136, 2, 65, 2, ''),
(71, 137, 2, 65, 3, ''),
(72, 27, 2, 4, 3, 'Gemeinsam mit ET, RGE'),
(73, 28, 2, 4, 4, 'IT, ET, RGE gemeinsam'),
(77, 19, 2, 24, 5, 'Zusammen mit IT'),
(81, 62, 1, 37, 3, ''),
(82, 84, 1, 118, 5, ''),
(83, 22, 1, 65, 6, 'Zusammen mit ET'),
(84, 10, 2, 96, 6, 'ET und IT zusammen planen!'),
(86, 57, 1, 114, 6, ''),
(87, 21, 1, 96, 4, 'Gemeinsam mit ET'),
(88, 18, 2, 54, 4, 'Nacheinander mit HFT'),
(89, 1, 2, 24, 6, ''),
(92, 4, 2, 126, 4, 'ET, RGE zusammen planen!'),
(98, 5, 2, 63, 5, ''),
(99, 8, 2, 21, 6, 'Beisitzer: Herr Hansmeier\n\nfrüher: Elektrische Energieerzeugung und -verteilung II\n\nET, RGE zusammen planen'),
(100, 9, 2, 126, 3, ''),
(101, 13, 2, 24, 6, ''),
(103, 15, 2, 40, 5, ''),
(105, 20, 2, 96, 6, ''),
(107, 24, 2, 56, 5, 'Zusammen mit PTV bei RGE'),
(108, 26, 2, 96, 4, ''),
(111, 31, 2, 65, 1, ''),
(112, 71, 1, 118, 4, ''),
(114, 88, 4, 45, 5, ''),
(115, 89, 4, 21, 6, 'Beisitzer: Herr Hansmeier'),
(116, 94, 4, 45, 2, ''),
(117, 95, 4, 45, 1, ''),
(118, 109, 4, 45, 3, ''),
(119, 115, 4, 56, 1, ''),
(120, 120, 4, 45, 4, ''),
(121, 121, 4, 56, 6, ''),
(124, 127, 4, 21, 6, ''),
(125, 22, 2, 65, 6, ''),
(126, 160, 4, 63, 3, ''),
(127, 25, 2, 63, 4, 'Gemeinsam mit RT (IT).'),
(128, 222, 4, 63, 4, ''),
(129, 158, 4, 126, 6, ''),
(130, 5, 4, 63, 5, ''),
(131, 3, 4, 21, 5, ''),
(132, 2, 4, 21, 5, 'ET, RGE zusammen planen'),
(133, 98, 4, 56, 2, ''),
(134, 166, 4, 21, 1, ''),
(135, 167, 4, 21, 2, ''),
(137, 103, 4, 102, 3, ''),
(138, 104, 4, 38, 4, ''),
(140, 138, 4, 38, 1, ''),
(142, 139, 4, 38, 2, ''),
(144, 21, 4, 96, 3, 'RGE, IT, ET zusammen planen'),
(145, 21, 2, 96, 3, ''),
(146, 24, 4, 56, 6, 'ET, RGE zusammen planen'),
(147, 142, 4, 22, 1, ''),
(148, 130, 2, 86, 1, ''),
(149, 143, 4, 81, 2, ''),
(150, 131, 2, 86, 2, 'Gemeinsam mit Phy2 (IT).'),
(151, 29, 4, 22, 6, ''),
(152, 30, 4, 22, 6, 'ET, RGE zusammen planen'),
(153, 159, 2, 63, 3, 'ET und IT zusammen planen'),
(154, 10, 1, 96, 5, 'ET und IT zusammen planen!'),
(155, 99, 1, 118, 2, ''),
(159, 144, 1, 78, 2, ''),
(160, 145, 1, 102, 5, ''),
(163, 148, 2, 118, 6, 'Gemeinsam mit INI'),
(165, 150, 4, 40, 3, 'Erstprüfer: Hilde\n\nZweitprüfer: Warmeling '),
(166, 151, 4, 40, 4, 'FIN bei WING mirschreiben'),
(167, 152, 2, 74, 4, 'Das äquivalente Modul beim SG WIW'),
(169, 154, 2, 127, 3, 'Parallel mit IT'),
(173, 168, 1, 127, 3, 'Parallel mit ET'),
(174, 169, 1, 43, 3, ''),
(175, 170, 4, 56, 4, ''),
(176, 171, 2, 22, 5, ''),
(178, 173, 4, 4, 4, ''),
(179, 174, 5, 118, 1, 'Beisitzer: Hr. Engert'),
(180, 175, 5, 65, 1, ''),
(181, 176, 4, 126, 5, 'Parallel mit ET'),
(182, 177, 4, 22, 5, 'Hr. Tolstykh'),
(183, 178, 5, 24, 1, ''),
(184, 179, 5, 53, 1, ''),
(185, 180, 5, 96, 2, ''),
(187, 182, 5, 63, 2, ''),
(188, 183, 5, 18, 2, ''),
(190, 185, 5, 38, 1, ''),
(191, 186, 5, 22, 2, ''),
(192, 187, 5, 21, 2, 'Beisitzer: Hr. Hansmeier'),
(193, 188, 1, 22, 5, 'Zusammen mit ET'),
(194, 189, 1, 13, 1, ''),
(195, 190, 4, 4, 3, ''),
(197, 192, 4, 56, 4, ''),
(199, 194, 4, 45, 4, ''),
(200, 196, 4, 21, 3, ''),
(201, 197, 2, 21, 4, 'und Energietechnik (BPO07)'),
(202, 198, 1, 13, 4, 'BPO 2007'),
(205, 201, 4, 21, 5, ''),
(206, 202, 1, 43, 4, ''),
(208, 204, 1, 78, 6, ''),
(209, 205, 2, 78, 6, ''),
(211, 207, 2, 63, 5, 'und ATE'),
(212, 208, 2, 38, 5, 'früher: Windenergieanlagen'),
(213, 209, 4, 38, 5, ''),
(214, 210, 4, 95, 6, ''),
(216, 212, 4, 63, 5, ''),
(217, 214, 4, 56, 6, ''),
(219, 216, 4, 38, 6, ''),
(220, 217, 5, 101, 2, ''),
(225, 224, 2, 106, 1, 'SG ELE'),
(226, 225, 2, 106, 2, 'SG ELE'),
(227, 226, 2, 108, 1, ''),
(228, 227, 4, 56, 5, ''),
(229, 228, 1, 106, 5, ''),
(230, 229, 2, 21, 6, ''),
(231, 230, 2, 21, 4, ''),
(232, 231, 2, 21, 4, ''),
(233, 232, 2, 38, 5, ''),
(234, 233, 5, 56, 1, ''),
(235, 234, 1, 43, 1, ''),
(236, 235, 1, 43, 2, ''),
(237, 236, 1, 121, 2, ''),
(238, 237, 4, 73, 6, ''),
(240, 250, 1, 102, 1, ''),
(241, 251, 1, 102, 2, ''),
(242, 252, 4, 56, 5, 'RGE-Wahlmodul'),
(244, 257, 1, 102, 6, ''),
(245, 258, 1, 102, 4, ''),
(246, 259, 2, 86, 4, ''),
(247, 262, 4, 56, 5, ''),
(248, 264, 1, 78, 1, ''),
(249, 272, 15, 61, 1, ''),
(251, 274, 15, 78, 1, ''),
(252, 275, 10, 38, 5, ''),
(253, 276, 10, 141, 1, ''),
(254, 277, 10, 22, 1, '');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `studiengang`
--

CREATE TABLE `studiengang` (
  `SGID` int(5) NOT NULL,
  `SGName` varchar(30) NOT NULL,
  `SGKurz` varchar(30) NOT NULL,
  `Semester` int(1) NOT NULL,
  `FK_FBID` int(5) NOT NULL,
  `FK_SPSID` int(4) NOT NULL DEFAULT 69
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `studiengang`
--

INSERT INTO `studiengang` (`SGID`, `SGName`, `SGKurz`, `Semester`, `FK_FBID`, `FK_SPSID`) VALUES
(1, 'Ingenieurinformatik_BA', 'INI', 6, 9, 91),
(2, 'Elektrotechnik_BA', 'ELE', 6, 9, 91),
(4, 'Regenerative Energien_BA', 'RGE', 6, 9, 91),
(5, 'Elektrotechnik_MA', 'MEL', 2, 9, 91),
(6, 'Maschinenbau_BA', 'MAB', 6, 9, 91),
(9, 'Maschinenbau_MA', 'MMB', 2, 9, 91),
(10, 'Elektrotechnik_VB', 'ELV', 8, 8, 91),
(11, 'Optimierung und Simulation_MA', 'OUS', 2, 9, 91),
(13, 'Mechatronik_BA', 'MEC', 6, 9, 91),
(14, 'Digitale Technologien_BA', 'DIT', 6, 6, 91),
(15, 'Biomechatronik_MA', 'BME', 4, 9, 91),
(16, 'Maschinenbau_VB', 'MAV', 8, 8, 91),
(17, 'Mathematik_BA', 'MAT', 6, 9, 91);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `stundenplaneintrag`
--

CREATE TABLE `stundenplaneintrag` (
  `SPID` int(11) NOT NULL,
  `SPEStartZeit` datetime NOT NULL,
  `SPEEndZeit` datetime NOT NULL,
  `SPTermin` int(1) NOT NULL,
  `FK_SGMID` int(11) DEFAULT NULL,
  `FK_LVID` int(11) DEFAULT NULL,
  `FK_RID` int(5) DEFAULT NULL,
  `Studierendenzahl` int(5) NOT NULL,
  `ZeitStempel` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `stundenplaneintrag`
--

INSERT INTO `stundenplaneintrag` (`SPID`, `SPEStartZeit`, `SPEEndZeit`, `SPTermin`, `FK_SGMID`, `FK_LVID`, `FK_RID`, `Studierendenzahl`, `ZeitStempel`) VALUES
(2, '2020-12-15 06:00:00', '2020-12-15 07:30:00', 1, 49, 1, 24, 30, '2020-12-15 17:15:46'),
(3, '2020-12-15 08:00:00', '2020-12-15 08:45:00', 1, 49, 2, 24, 30, '2020-12-15 17:16:48');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `stundenplansemester`
--

CREATE TABLE `stundenplansemester` (
  `SPSID` int(4) NOT NULL,
  `SPSemester` char(4) NOT NULL,
  `SPJahr` int(4) DEFAULT NULL,
  `SPKw` int(2) NOT NULL,
  `StartDatum` date NOT NULL,
  `FK_SPSTID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `stundenplansemester`
--

INSERT INTO `stundenplansemester` (`SPSID`, `SPSemester`, `SPJahr`, `SPKw`, `StartDatum`, `FK_SPSTID`) VALUES
(68, 'SoSe', 2019, 0, '2019-07-08', 4),
(69, 'SoSe', 2019, 0, '2019-09-23', 4),
(75, 'WiSe', 2019, 5, '2020-01-27', 4),
(76, 'WiSe', 2019, 0, '2020-03-23', 4),
(79, 'SoSe', 2020, 0, '2020-06-29', 3),
(82, 'SoSe', 2020, 42, '2020-10-12', 3),
(91, 'WiSe', 2020, 5, '2021-02-01', 3);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `stundenplanstatus`
--

CREATE TABLE `stundenplanstatus` (
  `SPSTID` int(11) NOT NULL,
  `SPSTBezeichnung` varchar(256) COLLATE utf16_german2_ci NOT NULL,
  `SPSTHint` varchar(256) COLLATE utf16_german2_ci NOT NULL,
  `PColor` varchar(7) COLLATE utf16_german2_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_german2_ci;

--
-- Daten für Tabelle `stundenplanstatus`
--

INSERT INTO `stundenplanstatus` (`SPSTID`, `SPSTBezeichnung`, `SPSTHint`, `PColor`) VALUES
(1, 'Früher Vorschlag', 'Die derzeitigen Stundenplaneinträge sind noch nicht verwendbar. ', '#CD5C5C'),
(2, 'In Diskussion', 'Der Stundenplanentwurf befindet sich in der Diskussion mit den Prüfern.', '#F0E68C'),
(3, 'Veröffentlicht', 'Der Stundenplan wird/wurde im LSF veröffentlicht.', '#228B22'),
(4, 'Veraltet', 'Dieser Stundenplan wird nicht mehr verwendet.', '#7f0000'),
(5, 'Zukünftige Planung', 'Dieser Stundenplan hat keine Bedeutung für die aktuelle Planung.', '#33FF33');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`AccID`),
  ADD KEY `FK_accounts_GroupID` (`FK_GroupID`),
  ADD KEY `FK_FBID` (`FK_FBID`);

--
-- Indizes für die Tabelle `benutzergruppe`
--
ALTER TABLE `benutzergruppe`
  ADD PRIMARY KEY (`GroupID`);

--
-- Indizes für die Tabelle `dozenten`
--
ALTER TABLE `dozenten`
  ADD PRIMARY KEY (`DID`),
  ADD KEY `FK_AccID` (`FK_AccID`);

--
-- Indizes für die Tabelle `faculty`
--
ALTER TABLE `faculty`
  ADD PRIMARY KEY (`FBID`);

--
-- Indizes für die Tabelle `lehrveranstaltungsart`
--
ALTER TABLE `lehrveranstaltungsart`
  ADD PRIMARY KEY (`LVID`);

--
-- Indizes für die Tabelle `location`
--
ALTER TABLE `location`
  ADD PRIMARY KEY (`LID`);

--
-- Indizes für die Tabelle `modul`
--
ALTER TABLE `modul`
  ADD PRIMARY KEY (`ModID`),
  ADD KEY `fk_modul_pruefcodes1` (`PCID`);

--
-- Indizes für die Tabelle `raum`
--
ALTER TABLE `raum`
  ADD PRIMARY KEY (`RID`),
  ADD UNIQUE KEY `RName` (`RName`),
  ADD KEY `FK_LID` (`FK_LID`);

--
-- Indizes für die Tabelle `sgmodul`
--
ALTER TABLE `sgmodul`
  ADD PRIMARY KEY (`SGMID`),
  ADD KEY `fk_sgmodul_modul1` (`FK_ModID`),
  ADD KEY `fk_sgmodul_studiengang1` (`FK_SGID`),
  ADD KEY `fk_sgmodul_pruefer1` (`FK_DID`);

--
-- Indizes für die Tabelle `studiengang`
--
ALTER TABLE `studiengang`
  ADD PRIMARY KEY (`SGID`),
  ADD KEY `FFBID` (`FK_FBID`),
  ADD KEY `FK_SPSID` (`FK_SPSID`);

--
-- Indizes für die Tabelle `stundenplaneintrag`
--
ALTER TABLE `stundenplaneintrag`
  ADD PRIMARY KEY (`SPID`),
  ADD KEY `FK_RID` (`FK_RID`),
  ADD KEY `FK_SGMID` (`FK_SGMID`),
  ADD KEY `FK_LVID` (`FK_LVID`);

--
-- Indizes für die Tabelle `stundenplansemester`
--
ALTER TABLE `stundenplansemester`
  ADD PRIMARY KEY (`SPSID`),
  ADD KEY `FK_SPSTID` (`FK_SPSTID`);

--
-- Indizes für die Tabelle `stundenplanstatus`
--
ALTER TABLE `stundenplanstatus`
  ADD PRIMARY KEY (`SPSTID`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `accounts`
--
ALTER TABLE `accounts`
  MODIFY `AccID` int(6) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=56;

--
-- AUTO_INCREMENT für Tabelle `benutzergruppe`
--
ALTER TABLE `benutzergruppe`
  MODIFY `GroupID` tinyint(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT für Tabelle `faculty`
--
ALTER TABLE `faculty`
  MODIFY `FBID` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT für Tabelle `lehrveranstaltungsart`
--
ALTER TABLE `lehrveranstaltungsart`
  MODIFY `LVID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT für Tabelle `location`
--
ALTER TABLE `location`
  MODIFY `LID` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT für Tabelle `modul`
--
ALTER TABLE `modul`
  MODIFY `ModID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=283;

--
-- AUTO_INCREMENT für Tabelle `raum`
--
ALTER TABLE `raum`
  MODIFY `RID` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT für Tabelle `sgmodul`
--
ALTER TABLE `sgmodul`
  MODIFY `SGMID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=258;

--
-- AUTO_INCREMENT für Tabelle `studiengang`
--
ALTER TABLE `studiengang`
  MODIFY `SGID` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT für Tabelle `stundenplaneintrag`
--
ALTER TABLE `stundenplaneintrag`
  MODIFY `SPID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT für Tabelle `stundenplansemester`
--
ALTER TABLE `stundenplansemester`
  MODIFY `SPSID` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=106;

--
-- AUTO_INCREMENT für Tabelle `stundenplanstatus`
--
ALTER TABLE `stundenplanstatus`
  MODIFY `SPSTID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `accounts`
--
ALTER TABLE `accounts`
  ADD CONSTRAINT `accounts_ibfk_1` FOREIGN KEY (`FK_GroupID`) REFERENCES `benutzergruppe` (`GroupID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `accounts_ibfk_2` FOREIGN KEY (`FK_FBID`) REFERENCES `faculty` (`FBID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `dozenten`
--
ALTER TABLE `dozenten`
  ADD CONSTRAINT `dozenten_ibfk_1` FOREIGN KEY (`FK_AccID`) REFERENCES `accounts` (`AccID`);

--
-- Constraints der Tabelle `raum`
--
ALTER TABLE `raum`
  ADD CONSTRAINT `raum_ibfk_1` FOREIGN KEY (`FK_LID`) REFERENCES `location` (`LID`);

--
-- Constraints der Tabelle `sgmodul`
--
ALTER TABLE `sgmodul`
  ADD CONSTRAINT `sgmodul_ibfk_1` FOREIGN KEY (`FK_ModID`) REFERENCES `modul` (`ModID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `sgmodul_ibfk_2` FOREIGN KEY (`FK_DID`) REFERENCES `dozenten` (`DID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `sgmodul_ibfk_3` FOREIGN KEY (`FK_SGID`) REFERENCES `studiengang` (`SGID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `studiengang`
--
ALTER TABLE `studiengang`
  ADD CONSTRAINT `studiengang_ibfk_1` FOREIGN KEY (`FK_FBID`) REFERENCES `faculty` (`FBID`),
  ADD CONSTRAINT `studiengang_ibfk_2` FOREIGN KEY (`FK_SPSID`) REFERENCES `stundenplansemester` (`SPSID`);

--
-- Constraints der Tabelle `stundenplaneintrag`
--
ALTER TABLE `stundenplaneintrag`
  ADD CONSTRAINT `stundenplaneintrag_ibfk_2` FOREIGN KEY (`FK_RID`) REFERENCES `raum` (`RID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `stundenplaneintrag_ibfk_3` FOREIGN KEY (`FK_SGMID`) REFERENCES `sgmodul` (`SGMID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `stundenplaneintrag_ibfk_4` FOREIGN KEY (`FK_LVID`) REFERENCES `lehrveranstaltungsart` (`LVID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `stundenplansemester`
--
ALTER TABLE `stundenplansemester`
  ADD CONSTRAINT `stundenplansemester_ibfk_1` FOREIGN KEY (`FK_SPSTID`) REFERENCES `stundenplanstatus` (`SPSTID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
