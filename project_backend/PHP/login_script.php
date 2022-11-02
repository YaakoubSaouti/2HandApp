<?php
header('Content-Type: application/json');
include("config.php");
function generateRandomString($length = 25) {
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $charactersLength = strlen($characters);
    $randomString = '';
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $characters[rand(0, $charactersLength - 1)];
    }
    return $randomString;
}

session_start();
//Are the variables empty?
if(
    !isset($_POST['username'])  
    || !isset($_POST['password'])
){
    echo '{"error_code": 5}';
    exit();
}
//Are the credentials correct?
$cc = false;
$un = $_POST['username'];
$pw = $_POST['password'];
$sql = "SELECT id,username,passwd FROM consumer WHERE username=? AND passwd=?";
$stm = $dbh->prepare($sql);
$stm->execute(array($un,$pw));
if($res=$stm->fetch()) {
    $id=$res['id'];
    $cc=true;
}

if($cc){
    $ae=false;
    $token = "2NDHAND-";
    $token .= generateRandomString(16);
    $sql="SELECT consumer_id FROM token WHERE consumer_id=?";
    $stm=$dbh->prepare($sql);
    $stm->execute(array($id));
    if($res=$stm->fetch()) $ae=true;
    if(!$ae){
        $sql="INSERT INTO token(consumer_id,token) VALUES(?,?)";
        $stm=$dbh->prepare($sql);
        $stm->execute(array($id,$token));
        echo '{"token" :'.$token.'}';
    }else{
        $sql="UPDATE token SET token=? WHERE consumer_id=?";
        $stm=$dbh->prepare($sql);
        $stm->execute(array($token,$id));
        echo '{"token" :'.$token.'}';
    }
    exit();
}
echo '{"error_code" : 6}';
?>