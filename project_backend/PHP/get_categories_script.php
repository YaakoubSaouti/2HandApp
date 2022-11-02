<?php
header('Content-Type: application/json');
include("config.php");
echo '{ "categories" : ';
$sql="SELECT title FROM category";
$stm=$dbh->prepare($sql);
$stm->execute();
$categories=array();
while($res=$stm->fetch()){
    $categories[]=$res['title'];
}
echo '[';
for($i=0 ; $i<count($categories)-1;$i++){
    echo '"'.$categories[$i].'",';
}
echo '"'.$categories[count($categories)-1].'"'."]}";
?>