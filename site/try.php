<!DOCTYPE html>
	<html lang="en">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
		<title>Silent places at the Rolex Learning Center</title>
		<link rel="stylesheet" type="text/css" href="style/map.css" />
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	</head>
<body>
	<?php
		error_reporting(E_ALL); 
	  	ini_set("display_errors", "1");
	    include("ffdb.inc.php");	// Flat File DataBase: .met file holds the schema and .dat file holds the data
	    class Area{
	    	var $x = "";
	    	var $y = "";
	    	var $noiseavg = 0;	// avg
	    	var $n = 0;			// number of measurements
		    function setData($x, $y){
		        $this->x = $x;
		        $this->y = $y; 
		        return $this;
		    }
		    function addMeasurement($noise){
		    	$this->n = $this->n + 1;
		        $this->noiseavg = $this->noiseavg + ($noise - $this->noiseavg)/$this->n;	  
		        return $this;
		    }
	    }
	    class Areas{
	    	var $arrayOfAreas = array();
	    	var $max = -200;	// necessary
		    function add($x, $y, $noise){		    	
				$found = false;
		    	foreach($this->arrayOfAreas as $area){
		    		if($area->x==$x && $area->y==$y){
		    			$area->addMeasurement($noise);
		    			$found = true;
		    		}		    			
		    	}	 						    	
		    	if(!$found){
		    		$ar = new Area();
					array_push($this->arrayOfAreas, $ar->setData($x,$y)->addMeasurement($noise));
		    	}						    	
		        return $this;
		    }
		    function toString(){
		    	$measurements = "";
		    	foreach($this->arrayOfAreas as $area){
					$decibel = $area->noiseavg + 40;	// adds reference value -40dB for silence to make values positive
					if($this->max < $decibel)
						$this->max = $decibel;				// the maximum between the averages
					$x = $area->x;
					$y = $area->y;
					$measurements = $measurements."{x: $x, y: $y, count: $decibel},\n";
		    	}
		    	return $measurements;
			}		    
	    }
	    $ar = new Areas();
	    $ar->add(24,34,-30);
		$ar->add(24,34,-60);
		//var_dump($ar);
		echo $ar->toString();
	?>
</body>
</html>
