<?php
header('Content-Type: application/json');
include("config.php");
//Are the credentials correct?
$token = $_GET['token'];
$sql="SELECT token FROM token WHERE token=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($token));
if($res=$stm->fetch()){
    echo '{"response": true }';
    exit();
}
echo '{"response": false }';
?>