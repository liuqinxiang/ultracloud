<%@ page language="java" contentType="text/html; charset=gbk"
    pageEncoding="gbk"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gbk">
<title></title>
<script type="text/javascript">
	function showDialog(msg, isCloseTop){
		var isClose = arguments[1] ? arguments[1] : false;//默认不关闭 
		top.hangge();
		if (isClose){
			$('#msg_div2').text(msg);
			$('#msgModal2').modal('show');
		}else{
			$('#msg_div').text(msg);
			$('#msgModal').modal('show');
		}
	}
	
	function closeDialog(){
		$('#msgModal').modal('hide');
		//top.Dialog.close();
	}
	
	function closeDialog2(){
		$('#msgModal2').modal('hide');
		top.Dialog.close();
	}
</script>
</head>
<body>
<!-- 模态框（Modal） -->
<div class="modal fade" id="msgModal" tabindex="-1" role="dialog" aria-labelledby="msgModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="msgModalLabel">提示信息</h4>
            </div>
            <div class="modal-body" id="msg_div"></div>
            <div class="modal-footer">
                <button type="button" id="msg_btn" class="btn btn-default" onclick="closeDialog();" data-dismiss="modal">确定</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<!-- 模态框（Modal） -->
<div class="modal fade" id="msgModal2" tabindex="-1" role="dialog" aria-labelledby="msgModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="msgModalLabel">提示信息</h4>
            </div>
            <div class="modal-body" id="msg_div2"></div>
            <div class="modal-footer">
                <button type="button" id="msg_btn" class="btn btn-default" onclick="closeDialog2();" data-dismiss="modal">确定</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>
</body>
</html>