﻿<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<base href="<%=basePath%>">
<!-- 下拉框 -->
<link rel="stylesheet" href="static/ace/css/chosen.css" />
<!-- jsp文件头和头部 -->
<%@ include file="../system/index/top.jsp"%>
<!-- 日期框 -->
<link rel="stylesheet" href="static/ace/css/datepicker.css" />
</head>
<body class="no-skin">

	<!-- /section:basics/navbar.layout -->
	<div class="main-container" id="main-container">
		<!-- /section:basics/sidebar -->
		<div class="main-content">
			<div class="main-content-inner">
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
						
						<!-- 检索  -->
						<form action="kvm/goListVirtualmachine.do" method="post" name="userForm" id="userForm">
						<input name="xzvalue" id="xzvalue" value="" type="hidden" />
						<input name="hostmachine_id" id="hostmachine_id" value="${pd.hostmachine_id} }" type="hidden" />
						<table style="margin-top:5px;width:100%;">
							<tr>
								<td style="vertical-align:top;">
									<a class="btn btn-mini btn-primary" onclick="top.Dialog.close();">返回</a>
									<a class="btn btn-mini btn-primary" onclick="handle('startup');">开机</a>
									<a class="btn btn-mini btn-danger" onclick="handle('shutdown');">关机</a>
									<a class="btn btn-mini btn-danger" onclick="handle('delete');">删除</a>
									<a class="btn btn-mini btn-primary" onclick="handle('restart');">重启</a>
									<a class="btn btn-mini btn-danger" onclick="handle('hangup');">挂起</a>
									<a class="btn btn-mini btn-primary" onclick="handle('recover');">恢复</a>
								</td>
								<td>
									<div class="nav-search" style="float: right;padding-top: 0px;margin-top: 0px;">
									<span class="input-icon" >
										<input class="nav-search-input" autocomplete="off" id="nav-search-input" type="text" name="keywords" value="${pd.keywords }" placeholder="关键词搜索" />
										<i class="ace-icon fa fa-search nav-search-icon"></i>
									</span>
									</div>
								</td>
								<c:if test="${QX.cha == 1 }">
								<td style="vertical-align:top;padding-left:2px;width: 32px;"><a class="btn btn-light btn-xs" onclick="searchs();"  title="检索"><i id="nav-search-icon" class="ace-icon fa fa-search bigger-110 nav-search-icon blue"></i></a></td>
								</c:if>
							</tr>
						</table>
						<!-- 检索  -->
					
						<table id="simple-table" class="table table-striped table-bordered table-hover"  style="margin-top:5px;">
							<thead>
								<tr>
									<th class="center" style="width:35px;">
									<label class="pos-rel"><input type="checkbox" class="ace" id="zcheckbox" /><span class="lbl"></span></label>
									</th>
									<th class="center">虚拟机名称</th>
									<th class="center">IP</th>
									<th class="center">CPU</th>
									<th class="center">内存</th>
									<th class="center">磁盘</th>
									<th class="center">状态</th>
								</tr>
							</thead>
													
							<tbody>
								
							<!-- 开始循环 -->	
							<c:choose>
								<c:when test="${not empty varList}">
									<c:if test="${QX.cha == 1 }">
									<c:forEach items="${varList}" var="var" varStatus="vs">
												
										<tr>
											<td class='center'>
												<label class="pos-rel"><input type='checkbox' name='ids' value="${var.id}" class="ace" /><span class="lbl"></span></label>
											</td>
											<td class="center">${var.name }</td>
											<td class="center">${var.ip }</td>
											<td class="center">${var.cpu }</td>
											<td class="center">${var.memory }</td>
											<td class="center">${var.datadisk }</td>
											<td class="center">${var.status }</td>
										</tr>
									
									</c:forEach>
									</c:if>
									<c:if test="${QX.cha == 0 }">
										<tr>
											<td colspan="10" class="center">您无权查看</td>
										</tr>
									</c:if>
								</c:when>
								<c:otherwise>
									<tr class="main_info">
										<td colspan="10" class="center">没有相关数据</td>
									</tr>
								</c:otherwise>
							</c:choose>
							</tbody>
						</table>
						
					<div class="page-header position-relative">
						<table style="width:100%;">
							<tr>
								<td style="vertical-align:top;"><div class="pagination" style="float: right;padding-top: 0px;margin-top: 0px;">${page.pageStr}</div></td>
							</tr>
						</table>
					</div>
					
					</form>
	
						</div>
						<!-- /.col -->
					</div>
					<!-- /.row -->
				</div>
				<!-- /.page-content -->
			</div>
		</div>
		<!-- /.main-content -->

		<!-- 返回顶部 -->
		<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
			<i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
		</a>

	</div>
	<!-- /.main-container -->

	<!-- basic scripts -->
	<!-- 页面底部js¨ -->
	<%@ include file="../system/index/foot.jsp"%>
	<!-- 删除时确认窗口 -->
	<script src="static/ace/js/bootbox.js"></script>
	<!-- ace scripts -->
	<script src="static/ace/js/ace/ace.js"></script>
	<!-- 日期框 -->
	<script src="static/ace/js/date-time/bootstrap-datepicker.js"></script>
	<!-- 下拉框 -->
	<script src="static/ace/js/chosen.jquery.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	</body>

<script type="text/javascript">
$(top.hangge());

//检索
function searchs(){
	top.jzts();
	$("#userForm").submit();
}

$(function() {
	//复选框全选控制
	var active_class = 'active';
	$('#simple-table > thead > tr > th input[type=checkbox]').eq(0).on('click', function(){
		var th_checked = this.checked;//checkbox inside "TH" table header
		$(this).closest('table').find('tbody > tr').each(function(){
			var row = this;
			if(th_checked) $(row).addClass(active_class).find('input[type=checkbox]').eq(0).prop('checked', true);
			else $(row).removeClass(active_class).find('input[type=checkbox]').eq(0).prop('checked', false);
		});
	});
});

//选择确定
function select(){
	var str = '';
	for(var i=0;i < document.getElementsByName('ids').length;i++){
	  if(document.getElementsByName('ids')[i].checked){
	  	if(str=='') str += document.getElementsByName('ids')[i].value;
	  	else str += ',' + document.getElementsByName('ids')[i].value;
	  }
	}
	if(str==''){
		bootbox.dialog({
			message: "<span class='bigger-110'>您没有选择任何内容!</span>",
			buttons: 			
			{ "button":{ "label":"确定", "className":"btn-sm btn-success"}}
		});
		$("#zcheckbox").tips({
			side:1,
            msg:'点这里全选',
            bg:'#AE81FF',
            time:8
        });
		return;
	}else{
		$("#xzvalue").val(str);
		top.Dialog.close();
	}
}

//对虚拟机的操作
function handle(type) {
	var msg;
	if(type == 'startup') {
		msg = '开机';
	} else if(type == 'shutdown') {
		msg = '关机';
	} else if(type == 'delete') {
		msg = '删除';
	} else if(type == 'restart') {
		msg = '重启';
	} else if(type == 'hangup') {
		msg = '挂起';
	} else if(type == 'recover') {
		msg = '恢复';
	}
	
	bootbox.confirm("确定要进行" + msg + "操作吗?", function(result) {
		if(result) {
			top.jzts();
			var url = "<%=basePath%>virtualhandle/" + type + ".do";
			$.get(url,function(data){
				console.log(data + '-------------->');
				nextPage(${page.currentPage});
			});
		}
	});
}
	
</script>
</html>
