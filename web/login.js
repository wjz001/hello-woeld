function login(){
    var n=document.getElementById("name").value;
    var p=document.getElementById("mm").value;
    if(n == "wjz"&& p == 123){
        return true;
    }else{
        alert("帐号或密码错误！请重新输入...");
        return false;
    }}