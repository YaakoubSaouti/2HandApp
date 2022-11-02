<?php
header('Content-Type: application/json');
include("config.php");
if( !isset($_POST['token'])
){
    echo '{"error_code" : 5}';
}
$token = $_POST['token'];
//Is the user in session?
$id;
$sql="SELECT consumer_id,token FROM token WHERE token=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($token));
if($res=$stm->fetch()){$id=$res['consumer_id'];}
if(!isset($id)){
    echo '{"error_code": 6 }';
    exit();
}
$email="";
$pn="";
$sql="SELECT username,email,phone_number FROM consumer WHERE id=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($id));
if($res=$stm->fetch()){
    $username = $res['username'];
    if(!is_null($res['email'])) $email=$res['email'];
    if(!is_null($res['phone_number'])) $pn=$res['phone_number'];
}
$ids = array();
$titles = array();
$prices = array();
$images = array();
$sql="SELECT id,title,price,image_path FROM item WHERE consumer_id = ?";
$stm=$dbh->prepare($sql);
$stm->execute(array($id));
while($res=$stm->fetch()){
    $ids[]= $res[0];
    $titles[] = $res[1];
    $prices[] = $res[2];
    if(!is_null($res[3])){
        $decoded = file_get_contents('../images/'.$res[3]);
        $encoded = base64_encode($decoded);
    }else{
        $encoded="0";
    }
    $images[] = $encoded;
}
if(count($ids)==0){
    echo json_encode(array('username'=>$username,'email'=>$email,'pn'=>$pn));
    exit();
}
echo json_encode(array('username'=>$username,'email'=>$email,'pn'=>$pn,'ids' => $ids, 'titles' => $titles, 'prices' => $prices,'images'=>$images));
?>