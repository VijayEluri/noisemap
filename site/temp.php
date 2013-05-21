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
		<?php
			error_reporting(E_ALL); 
	  		ini_set("display_errors", "1");
	  		include("ffdb.inc.php");
	  		$db = new FFDB();
	  		$bssid = "bssid";
			$noise = "noise";
			$timestamp = "timestamp";
			if($db->open("noisepoints"))
				foreach($db->getall(NULL) as $item)
					echo $item[$bssid]." ".$item[$noise]."<br/>";
	  		echo 'finished';
		?>
	</body>
</html>
