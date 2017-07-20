$(document).ready(function () {
    $("#sc").click(function() {
        $("#www").hide();
    })
})
$(document).ready(function () {
    $("#hh").click(function(){
        var a=document.getElementById("hh1").value;
        $("#www").after("我对敏哥说: "  + a);

    })
})
$(document).ready(function () {
    $("#t11").click(function () {
        $("t1").innerHTML=document.getElementById("t1").value;
    })
})
function showImage() {
    var path=document.getElementById("xx").value;

    document.getElementById("x").src=path;

}
function register(){
    var a11=document.getElementById("pwd1").value;
    var a12=document.getElementById("pwd2").value;
    if(a11==a12 && a11!=""){
        return true;
    }else{
        alert("两次输入的密码不一样！")
        return false;
    }
}