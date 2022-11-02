<?php
function distance(
    $latitudeFrom, $longitudeFrom, $latitudeTo, $longitudeTo, $earthRadius = 6371000)
{
    // convert from degrees to radians
    $latFrom = deg2rad($latitudeFrom);
    $lonFrom = deg2rad($longitudeFrom);
    $latTo = deg2rad($latitudeTo);
    $lonTo = deg2rad($longitudeTo);

    $lonDelta = $lonTo - $lonFrom;
    $a = pow(cos($latTo) * sin($lonDelta), 2) +
        pow(cos($latFrom) * sin($latTo) - sin($latFrom) * cos($latTo) * cos($lonDelta), 2);
    $b = sin($latFrom) * sin($latTo) + cos($latFrom) * cos($latTo) * cos($lonDelta);

    $angle = atan2(sqrt($a), $b);
    return $angle * $earthRadius;
}
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
$sql="SELECT consumer_id,token,longitude,latitude FROM token,consumer WHERE token=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($token));
if($res=$stm->fetch()){
    $consumer_id=$res['consumer_id'];
    $long=$res['longitude'];
    $lat=$res['latitude'];
}
if(!isset($consumer_id)){
    echo '{"error_code": 6 }';
    exit();
}
$sql="SELECT a.title,a.price,a.infos,a.image_path,b.longitude,b.latitude,b.username,b.email,b.phone_number FROM item a,consumer b WHERE a.consumer_id = b.id AND a.id=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($id));
if($res=$stm->fetch()){
    $title = $res[0];
    $price = $res[1];
    $infos = $res[2];
    if(!is_null($res[3])){
        $decoded = file_get_contents('../images/'.$res[3]);
        $encoded = base64_encode($decoded);
    }else{
        $encoded="0";
    }
    $distance = intval(distance($lat,$long,$res[5],$res[4])/1000);
    $pseudo = $res[6];
    $email="";
    if($res[7]!=null){
        $email=$res[7];
    }
    $pn="";
    if($res[8]!=null){
        $pn=$res[8];
    }
}
if(!isset($title)){
    echo '{"error_code" : 7}';
    exit();
}
$sql="SELECT category.title FROM item,category WHERE  item.category_id=category.id AND item.id=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($id));
if($res=$stm->fetch()){
    $category=$res[0];
}
$flag=false;
$sql="SELECT consumer_id,item_id FROM wish WHERE consumer_id = ? AND item_id = ?";
$stm=$dbh->prepare($sql);
$stm->execute(array($consumer_id,$id));
if($res=$stm->fetch()){
    $flag=true;
}
echo json_encode(array(
    'title'=>$title,
    'price'=>$price,
    'infos'=>$infos,
    'image'=>$encoded,
    'distance'=>$distance,
    'pseudo'=>$pseudo,
    'email'=>$email,
    'pn'=>$pn,
    'category'=>$category,
    'wl'=>$flag
));
?>