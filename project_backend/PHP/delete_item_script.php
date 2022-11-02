<?php
header('Content-Type: application/json');
include("config.php");
if( !isset($_POST['token'])
    || !isset($_POST['id'])
){
    echo '{"error_code" : 5}';
    exit();
}
$token = $_POST['token'];
$id = $_POST['id'];
//Is the user in session?
$id;
$sql="SELECT consumer_id,token FROM token WHERE token=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($token));
if($res=$stm->fetch()){$consumer_id=$res['consumer_id'];}
if(!isset($id)){
    echo '{"error_code": 6 }';
    exit();
}
$sql="SELECT image_path FROM item WHERE consumer_id=? AND id=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($consumer_id,$id));
if($res=$stm->fetch()){
    unlink("../images/".$res['image_path']);
}
$sql="DELETE FROM item WHERE id=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($id));
$sql="DELETE FROM wish WHERE item_id=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($id));
echo '{"status": 0}';
?>