
var path = 'http://localhost:8222';
var mId = '1758660311418015744';

function queryList(){
    $.ajax({
        type : 'GET',
        url : path + '/view/fast/pay.html', //通过url传递name参数
        data : {
            mId : mId,
        },
        dataType : 'json',
        success:function(data){
            console.log(data)
        },
        error:function(e){
            alert("Net error ,try later.");
        }
    });
}
