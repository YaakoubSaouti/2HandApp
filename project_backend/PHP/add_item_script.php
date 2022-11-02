<?php
function validateTwoDecimals($number)
{
    $count=0;
    $number="$number";
    $decimalplaces=explode(".",$number);
    if(isset($decimalplaces[1])){
        $count=strlen($decimalplaces[1]);
    }
   if($count>2)
     return false;
   else
     return true;
}
header('Content-Type: application/json');
include("config.php");
//Are all the parameters set?
if(
    !isset($_POST['token'])
    || !isset($_POST['name'])
    || !isset($_POST['category'])
    || !isset($_POST['price'])
    || !isset($_POST['desc'])
){
    echo '{"error_code": 5 }';
    exit();
}
//Is the session active?
$token = $_POST['token'];
$consumer_id;
$sql="SELECT consumer_id,token FROM token WHERE token=?";
$stm=$dbh->prepare($sql);
$stm->execute(array($token));
if($res=$stm->fetch()){
    $consumer_id=$res['consumer_id'];
}
if(!isset($consumer_id)){
    echo '{"error_code": 6 }';
    exit();
}
$name=$_POST['name'];
$price=$_POST['price'];
$desc=$_POST['desc'];
$category=$_POST['category'];
$category_id;
$sql="SELECT id,title FROM category WHERE UPPER(title) = ?";
$stm=$dbh->prepare($sql);
$stm->execute(array($category));
if($res=$stm->fetch()){
    $category_id = $res['id'];
}
if(!isset($category_id)){
    echo '{"error_code": 7 }';
    exit();
}
//Is the name too short?
if(strlen($name)<2){
    echo '{"error_code": 8 }';
    exit();
}
//Is the price a number and in the correct format?
if(!is_numeric($price) || !validateTwoDecimals($price) || $price<0){
    echo '{"error_code": 9 }';
    exit();
}
//Is the description too short?
if(strlen($desc) < 10){
    echo '{"error_code": 10 }';
    exit();
}
//All the conditions are met -> We Insert the item
$sql="INSERT INTO item(consumer_id,title,price,infos,category_id) VALUES(?,?,?,?,?)";
$stm=$dbh->prepare($sql);
$stm->execute(array($consumer_id,$name,$price,$desc,$category_id));
if(isset($_POST['img'])){
    $img = $_POST['img'];
    $img=str_replace('-','+',$img);
    $img=str_replace('_','/',$img);
    $data = base64_decode($img);
    $last_id;
    $sql="SELECT LAST_INSERT_ID()";
    $stm=$dbh->prepare($sql);
    $stm->execute();
    if($res=$stm->fetch()){
        $last_id=$res[0];
    }
    $image_path="$last_id.jpeg";
    $file = "../images/$image_path";
    $sql="UPDATE item SET image_path = ? WHERE id = ?";
    $stm=$dbh->prepare($sql);
    $stm->execute(array($image_path,$last_id));
    file_put_contents($file,$data);
}
echo '{"status": 0}';
?>