--
-- Table structure for table `INSTRUMENT`
--

CREATE TABLE INSTRUMENT (
  INSTRUMENT_NO INT NOT NULL DEFAULT 0,
  INSTRUMENT_TYPE varchar(50) NOT NULL DEFAULT '',
  INSTRUMENT_NAME varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (INSTRUMENT_NO)
);

--
-- Dumping data for table `INSTRUMENT`
--

INSERT INTO INSTRUMENT VALUES 
(3,'LC-ESI-IT','LC/MSD Trap XCT, Agilent Technologies'),
(1,'LC-ESI-QQ','API3000, Applied Biosystems'),
(2,'LC-ESI-QTOF','Qstar, Applied Biosystems');


