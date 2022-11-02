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
    || !isset($_POST['longitude'])
    || !isset($_POST['latitude'])
    || !isset($_POST['distance'])
){
    echo '{"error_code" : 5}';
}
$token = $_POST['token'];
$long = $_POST['longitude'];
$lat = $_POST['latitude'];
$distance = $_POST['distance'];
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
//Are the coordinates valid?
if(
    !is_numeric($long) 
    || !is_numeric($lat) 
    || !($long >= -180 && $long <= 180) 
    || !($lat >= -90 && $lat <= 90)
){
    echo '{"error_code": 7}';
    exit();
}else{
    $sql="UPDATE consumer SET longitude=?,latitude=? WHERE id=?";
    $stm=$dbh->prepare($sql);
    $stm->execute(array($long,$lat,$id));
}
//Is the distance valid?
if(
    !is_numeric($distance) 
    || $distance<0
){
    echo '{"error_code": 8}';
    exit();
}
$ids = array();
$titles = array();
$prices = array();
$images = array();
$sql="SELECT a.id,a.title,a.price,a.image_path,b.longitude,b.latitude,b.id FROM item a,consumer b WHERE a.consumer_id = b.id";
$stm=$dbh->prepare($sql);
$stm->execute();
while($res=$stm->fetch()){
    if(distance($lat, $long, $res[5],$res[4])<intval($distance)*1000 && $res[6]!=$id){
        $ids[] = $res[0];
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
}
if(count($ids)==0){
    echo '{"error_code" : 9}';
    exit();
}
echo json_encode(array('ids' => $ids, 'titles' => $titles, 'prices' => $prices,'images'=>$images));
?>