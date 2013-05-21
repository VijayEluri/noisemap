<!DOCTYPE html>
<html lang="en">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
		<title>Silent places at the Rolex Learning Center</title>
	</head>
	<body>
	<!-- PHP 5.2 -->
		<?php
			error_reporting(E_ALL); 
		  	ini_set("display_errors", "1");
		    include("ffdb.inc.php");	// Flat File DataBase: .met file holds the schema and .dat file holds the data
			$db = new FFDB();
			$aps = simplexml_load_file('aps.xml');
			if(!empty($_POST) && $db->open("noisepoints")){
				$min = $_POST['min'];	// to debug: http://hurl.it/
				$max = $_POST['max'];	// if they are not null then append them in a file
				$bssid = "bssid";
				$noise = "noise";
				$timestamp = "timestamp";	// TODO: avg over the same area to reduce network IO
				foreach($db->getall(NULL) as $item){
					echo $item[$bssid].$item[$timestamp].$item[$noise];				
					if($item[$timestamp] > $min && $item[$timestamp] < $max)
						foreach($aps->entry as $entry)
							if($entry->string==$item[$bssid])
								foreach($entry->area as $area)
									echo $area->heigth." ".$area->width." ".$area->x." ".$area->y." ".$item[$noise]."\n";				
				}
			}
		?>
	</body>
</html>
