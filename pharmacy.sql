-- phpMyAdmin SQL Dump
-- version 4.5.2
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Aug 11, 2016 at 06:47 AM
-- Server version: 5.7.9
-- PHP Version: 5.6.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pharmacy`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
CREATE TABLE IF NOT EXISTS `admin` (
  `admin_id` int(12) NOT NULL AUTO_INCREMENT,
  `username` varchar(200) DEFAULT NULL,
  `password` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`admin_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`admin_id`, `username`, `password`) VALUES
(1, 'kelvin', 'kelvin');

-- --------------------------------------------------------

--
-- Table structure for table `clerks`
--

DROP TABLE IF EXISTS `clerks`;
CREATE TABLE IF NOT EXISTS `clerks` (
  `clerk_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(200) DEFAULT NULL,
  `password` varchar(200) DEFAULT NULL,
  `pnumber` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`clerk_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `clerks`
--

INSERT INTO `clerks` (`clerk_id`, `username`, `password`, `pnumber`) VALUES
(1, 'kelvin', 'kelvin', '+254702500937'),
(5, 'rose', 'rose', '0715964456'),
(3, 'jane', 'jane', '+2547000000'),
(4, 'James', 'maina', '+254890087654');

-- --------------------------------------------------------

--
-- Table structure for table `drugs`
--

DROP TABLE IF EXISTS `drugs`;
CREATE TABLE IF NOT EXISTS `drugs` (
  `drug_id` int(11) NOT NULL AUTO_INCREMENT,
  `units` varchar(100) DEFAULT NULL,
  `price` varchar(100) DEFAULT NULL,
  `drug_name` varchar(200) DEFAULT NULL,
  `supplier_name` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`drug_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `drugs`
--

INSERT INTO `drugs` (`drug_id`, `units`, `price`, `drug_name`, `supplier_name`) VALUES
(1, '20', '300', 'Panadol', 'KEMRI'),
(2, '20', '3500', 'Panadol', 'Theranos'),
(3, '40', '6000', 'Celastamine', 'Theranos');

-- --------------------------------------------------------

--
-- Table structure for table `duties`
--

DROP TABLE IF EXISTS `duties`;
CREATE TABLE IF NOT EXISTS `duties` (
  `duty_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(200) DEFAULT NULL,
  `pnumber` varchar(200) DEFAULT NULL,
  `date_recorded` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`duty_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `duties`
--

INSERT INTO `duties` (`duty_id`, `username`, `pnumber`, `date_recorded`) VALUES
(1, 'kelvin', '+254702500937', '8/8/2016'),
(2, 'Jane', '+0000000000', '8/8/2016'),
(4, 'Jane', '+0000000000', '8/9/2016');

-- --------------------------------------------------------

--
-- Table structure for table `patients`
--

DROP TABLE IF EXISTS `patients`;
CREATE TABLE IF NOT EXISTS `patients` (
  `patient_id` int(11) NOT NULL AUTO_INCREMENT,
  `fname` varchar(200) DEFAULT NULL,
  `sname` varchar(200) DEFAULT NULL,
  `tname` varchar(200) DEFAULT NULL,
  `pnumber` varchar(200) DEFAULT NULL,
  `email` varchar(200) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`patient_id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `patients`
--

INSERT INTO `patients` (`patient_id`, `fname`, `sname`, `tname`, `pnumber`, `email`, `address`) VALUES
(1, 'Kelvin', 'Kagia', 'Kimani', '+254702500937', 'kelvinkagia@gmail.com', '31-10106'),
(2, 'Derrick', 'Patrick', 'Muriithi', '+254702456399', 'deriuspatz@gmail.com', '31-10106'),
(3, 'James', 'Muthee', 'Waweru', '+2657806348', 'jamesmuthee@gmail.com', '54-10100'),
(4, 'Irene', 'Wambui', 'Maina', '+25479087645', 'irenewambui@gmail.com', '90-10100'),
(6, 'James', 'Mwangi', 'Wachira', '+2547809654', 'jamesmwangi@gmail.com', '10-10007');

-- --------------------------------------------------------

--
-- Table structure for table `prescription`
--

DROP TABLE IF EXISTS `prescription`;
CREATE TABLE IF NOT EXISTS `prescription` (
  `prescription_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_email` varchar(200) DEFAULT NULL,
  `patient_pnumber` varchar(200) DEFAULT NULL,
  `units` varchar(200) DEFAULT NULL,
  `price` varchar(200) DEFAULT NULL,
  `drug` varchar(200) DEFAULT NULL,
  `date_recorded` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`prescription_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `prescription`
--

INSERT INTO `prescription` (`prescription_id`, `patient_email`, `patient_pnumber`, `units`, `price`, `drug`, `date_recorded`) VALUES
(1, 'irenewambui@gmail.com', '+25479087645', '30', '200', 'Panadol', '8/5/2016'),
(2, 'kelvinkagia@gmail.com', '+254702500937', '5', '500', 'Celastamine', '8/8/2016'),
(3, 'jamesmwangi@gmail.com', '+2547809654', '10', '500', 'Panadol', '8/8/2016'),
(4, 'irenewambui@gmail.com', '+25479087645', '10', '78', 'Panadol', '8/9/2016');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
