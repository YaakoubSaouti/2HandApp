<?php
header('Content-Type: application/json');
include("config.php");
//Are the variables empty?
if(
    !isset($_POST['username'])  
    || !isset($_POST['password1'])
    || !isset($_POST['password2']) 
    || !isset($_POST['longitude']) 
    || !isset($_POST['latitude'])
    || (!isset($_POST['email']) && !isset($_POST['phone_number']))
){
    echo '{"error_code": 5}';
    exit();
}
//Are the values correct?
$un = $_POST['username'];
$pw1 = $_POST['password1'];
$pw2 = $_POST['password2'];
$long = $_POST['longitude'];
$lat = $_POST['latitude'];
$email_regex="/^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-z]+)$/";
$pn_regex="/^(?:(?:\+|0{0,2})91(\s*[\ -]\s*)?|[0]?)?[456789]\d{9}|(\d[ -]?){10}\d$/";
//username?
if(strlen($un) < 2){
    echo '{"error_code": 6}';
    exit();
}
$sql="SELECT username FROM consumer WHERE username=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($un));
if($res=$stm->fetch()){
    echo '{"error_code": 7}';
    exit();
}
//passwords?
if(strlen($pw1) < 8){
    echo '{"error_code": 8}';
    exit();
}
if(strcmp($pw1,$pw2) != 0){
    echo '{"error_code": 9}';
    exit();
}
//longitude and latitude?
if(
    !is_numeric($long) 
    || !is_numeric($lat) 
    || !($long >= -180 && $long <= 180) 
    || !($lat >= -90 && $lat <= 90)
){
    echo '{"error_code": 10}';
    exit();
}
//email?
$areset=0;
if(isset($_POST['email'])){
    if(!preg_match($email_regex,$_POST['email'])){
        echo '{"error_code": 11}';
        exit();
    }
    $areset = 1;
}
//phone_number?
if(isset($_POST['phone_number'])){
    if(!preg_match($pn_regex,$_POST['phone_number'])){
        echo '{"error_code": 12}';
        exit();
    }
    if($areset==1) $areset=3;
    else $areset = 2;
}
switch($areset){
    case 1:
        $stm=$dbh->prepare("INSERT INTO consumer(username,passwd,longitude,latitude,email) VALUES(?,?,?,?,?)");
	    $stm->execute(array($un,$pw1,$long,$lat,$_POST["email"]));
        break;
    case 2:
        $stm=$dbh->prepare("INSERT INTO consumer(username,passwd,longitude,latitude,phone_number) VALUES(?,?,?,?,?)");
	    $stm->execute(array($un,$pw1,$long,$lat,$_POST["phone_number"]));
        break;
    case 3:
        $stm=$dbh->prepare("INSERT INTO consumer(username,passwd,longitude,latitude,email,phone_number) VALUES(?,?,?,?,?,?)");
	    $stm->execute(array($un,$pw1,$long,$lat,$_POST["email"],$_POST["phone_number"]));
        break;
}
echo '{"status": 0}';
?>
