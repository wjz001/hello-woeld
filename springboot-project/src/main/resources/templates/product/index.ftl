<html>
<#include "../common/header.ftl">

<body>
<div id="wrapper" class="toggled">

<#--边栏sidebar-->
    <#include "../common/nav.ftl">

<#--主要内容content-->
    <div id="page-content-wrapper">
        <div class="container-fluid">
            <div class="row clearfix">
                <div class="col-md-12 column">
                    <form role="form" method="post" action="/sell/seller/product/save">
                        <div class="form-group">
                            <label>名称</label>
                            <input name="productName" class="form-control" type="text" value="${(productInfo.productName)!''}"/>
                        </div>
                        <div class="form-group">
                            <label>价格</label>
                            <input name="productPrice" type="text" class="form-control" value="${(productInfo.productPrice)!''}"/>
                        </div>
                        <div class="form-group">
                            <label>描述</label>
                            <input name="productDescription" type="text" class="form-control" value="${(productInfo.productDescription)!''}">
                        </div>
                        <div class="form-group">
                            <label>图片</label>
<#--                            <img height="100" width="100" src="${(productInfo.productIcon)!''}" alt="">-->
                            <input name="productIcon" id="productIcon" type="text" class="form-control" value="${(productInfo.productIcon)!''}"/>
<#--                            <form name="upform" action="" method="POST" enctype="multipart/form-data">-->
                                <input type ="file" name="myfile1" id="myfile1"/><br/>
                                <input type="button" value="确定" onclick="upload()"/><br/>
<#--                            </form>-->
                        </div>
                        <div class="form-group">
                            <label>类目</label>
                            <select name="categoryType" class="form-control">
                                <#list categoryList as category>
                                    <option value="${(category.categoryType)}"
                                            <#if (productInfo.categoryType)?? && productInfo.categoryType==category.categoryType>
                                                selected
                                            </#if>>
                                        ${category.categoryName}
                                    </option>
                                </#list>

                            </select>
                        </div>
                        <input hidden type="text" name="productId" value="${(productInfo.productId)!''}">

                        <button type="submit" class="btn btn-default">提交</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

</div>
</body>
</html>
<script src="/sell/js/jquery-3.4.0.js"></script>
<script>
    function upload() {
        var formData = new FormData();
        formData.append('file', $('#myfile1')[0].files[0]);  //添加图片信息的参数
        formData.append('sizeid',123);  //添加其他参数
        // formData = document.getElementById("myfile1").files[0];
        $.ajax({
            // contentType:"multipart/form-data",
            url:"/sell/thirdApi/qiniu/img",
            type:"POST",
            data:formData,
            cache:false,
            // dataType:"text",
            processData: false, // 告诉jQuery不要去处理发送的数据
            contentType: false, // 告诉jQuery不要去设置Content-Type请求头
            success: function(result){
                $("#productIcon").val(result);
                console.log(result);
            }
        });
    }
</script>