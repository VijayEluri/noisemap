<!DOCTYPE html>
<html lang="en">
	<head>
		<!-- for convenience: HTML5, not:
		<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
		<html xmlns="http://www.w3.org/1999/xhtml">	-->
		<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
		<title>Silent places at the Rolex Learning Center</title>
		<!-- <link rel="stylesheet" type="text/css" href="http://www.000webhost.com/images/index/styles.css" />	-->
	</head>
	
	<body>
	<!-- PHP 5.2 -->
		<?php	// to debug: http://hurl.it/
			error_reporting(E_ALL); 
	  		ini_set("display_errors", "1");
	        include("ffdb.inc.php");	// Flat File DataBase: .met file holds the schema and .dat file holds the data
			$db = new FFDB();
			if (!$db->open("noisepoints")){
			   $schema = array( 
			      array("id", FFDB_INT_AUTOINC, "key"),
			      array("bssid", FFDB_STRING),
			      array("noise", FFDB_STRING),
			      array("timestamp", FFDB_STRING),
			   );
			   if (!$db->create("noisepoints", $schema))
			      user_error("Check if you have write permissions on this folder\n");
			}
			if(!empty($_POST)){
				 if(isset($_POST['db'])){
				 	unlink('noisepoints.dat');
				 	unlink('noisepoints.met');
				 	echo("database emptied!\n");
				 }
				 else{
					$record["bssid"] = $_POST['bssid'];
					$record["noise"] = $_POST['noise'];
					$record["timestamp"] = $_POST['timestamp'];	
			       if (!$db->add($record))	// should be an error in java
			          echo("failed!\n");
			       else
			          echo("success!\n");
				 }
			}
		?>
	</body>
</html>
