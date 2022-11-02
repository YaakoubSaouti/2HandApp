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
$is_in_wishlist=false;
$sql="SELECT consumer_id,item_id FROM wish WHERE consumer_id=? AND item_id=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($consumer_id,$id));
if($res=$stm->fetch()){$is_in_wishlist=true;}
if(!$is_in_wishlist){
    $sql="INSERT INTO wish(consumer_id,item_id) VALUES(?,?)";
    $stm=$dbh->prepare($sql);
    $stm->execute(array($consumer_id,$id));
}else{
    $is_in_wishlist=false;
    $sql="DELETE FROM wish WHERE consumer_id=? AND item_id=?";
    $stm=$dbh->prepare($sql);
    $stm->execute(array($consumer_id,$id));
}
echo '{"status": 0}';
?>