<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
<!-- jsp文件头和头部 -->
<%@ include file="../system/index/top.jsp"%>
<jsp:include page="../msg/msg_dialog.jsp"></jsp:include>
</head>
<body class="no-skin">

	<button class="btn btn-success" id="cancel" name="cancel" type="reset"
		onclick="top.Dialog.close();">返回</button>
	<small>
		<button class="btn disabled btn-success">工单号：${workorder.appNo}</button>
	</small>

	</div>
	<!-- /.page-header -->
	<div class="alert alert-info">流程图</div>
	<p></p>

	<div>
		<img id="procImg"
			src="<c:url value='findworkflowPic.do?procDefId=${procDefId}' />" />
		<c:if test="${workorder.appNo != '5'}">
			<!-- 给执行的节点加框 -->
			<div
				style="position: absolute; border:2px solid red;left:${workorderImag.x}px;top:${workorderImag.y + 153}px;width:${workorderImag.width }px;height:${workorderImag.height }px;"></div>
		</c:if>
	</div>

	<div class="alert alert-info">申请工单详情</div>
	<div>
		<table id="simple-table"
			class="table table-striped table-bordered table-hover"
			style="margin-top: 5px;">
			<thead>
				<tr>
					<th class="center">虚拟机名称</th>
					<th class="center">地区代码</th>
					<th class="center">平台</th>
					<th class="center">部署类型</th>
					<th class="center">操作系统类型</th>
				</tr>
			</thead>

			<tbody>
				<tr>
					<td class='center'>${orderInfo.virName}</td>
					<td class='center'>${orderInfo.areaCode}</td>
					<td class='center'>${orderInfo.platType}</td>
					<td class='center'>${orderInfo.deployType}</td>
					<td class='center'>${orderInfo.osType}</td>
				</tr>
			</tbody>
		</table>
		<form class="form-horizontal" id="checkform" name="checkform"
			role="form">
			<div class="col-md-offset-3 col-md-9">
				<button class="btn btn-info" id="OK" name="OK" type="button"
					onclick="doCheck('${workorder.appNo}');">
					<i class="ace-icon fa fa-check bigger-110"></i> 确定执行
				</button>

				&nbsp; &nbsp; &nbsp;
				<button class="btn" id="cancel" name="cancel" type="reset"
					onclick="top.Dialog.close();">
					<i class="ace-icon fa fa-undo bigger-110"></i> 取消
				</button>
			</div>
		</form>
	</div>

	<div class="alert alert-info">部署执行详情</div>
	<div style="height: 150px; width: 100%"></div>

	<div class="alert alert-info">关联任务</div>
	<div>
		<table id="simple-table"
			class="table table-striped table-bordered table-hover"
			style="margin-top: 5px;">
			<thead>
				<tr>
					<th class="center">任务单号</th>
					<th class="center">任务处理人</th>
					<th class="center">任务状态</th>
					<th class="center">处理结果</th>
					<th class="center">处理时间</th>
				</tr>
			</thead>

			<tbody>
				<!-- 开始循环 -->
				<c:choose>
					<c:when test="${not empty relateTaskList}">
						<c:forEach items="${relateTaskList}" var="var" varStatus="vs">
							<tr>
								<td class='center'>${var.taskNo}</td>
								<td class='center'>${var.assignee}</td>
								<td class='center'>${var.status}</td>
								<td class='center'>${var.result}</td>
								<td class='center'>${var.time}</td>
							</tr>
						</c:forEach>
					</c:when>
				</c:choose>
			</tbody>
		</table>

	</div>



	<div class="page-header">
		<h1>
			工单审查 <small> <i class="ace-icon fa fa-angle-double-right"></i>
				工单号：${workorder.appNo}
			</small>
		</h1>
	</div>
	<!-- /.page-header -->

	<div class="row">
		<div class="col-sm-2"></div>
		<div class="col-sm-8">
			<!-- PAGE CONTENT BEGINS -->

			<div class="widget-header">
				<form class="form-horizontal" id="checkform" name="checkform"
					role="form">
					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right"
							for="form-field-1"> 工单编号: </label>

						<div class="col-sm-6">
							<label class="col-sm-3 control-label no-padding-right"
								for="form-field-1"> ${workorder.appNo} </label>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right"
							for="form-field-1"> 运维申请单编号: </label>

						<div class="col-sm-6">
							<label class="col-sm-3 control-label no-padding-right"
								for="form-field-1"> ${workorder.orderNo} </label>
						</div>
					</div>
					<div class="clearfix form-actions">
						<div class="col-md-offset-3 col-md-9">
							<button class="btn btn-info" id="OK" name="OK" type="button"
								onclick="doCheck('${workorder.appNo}');">
								<i class="ace-icon fa fa-check bigger-110"></i> 确定审查
							</button>

							&nbsp; &nbsp; &nbsp;
							<button class="btn" id="cancel" name="cancel" type="reset"
								onclick="top.Dialog.close();">
								<i class="ace-icon fa fa-undo bigger-110"></i> 取消
							</button>
						</div>
					</div>
				</form>
			</div>
		</div>
		<div class="col-sm-2"></div>
	</div>

	<!-- 返回顶部 -->
	<a href="#" id="btn-scroll-up"
		class="btn-scroll-up btn btn-sm btn-inverse"> <i
		class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
	</a>
	<!-- /.main-container -->

	<!-- basic scripts -->
	<!-- 页面底部js¨ -->
	<%@ include file="../system/index/foot.jsp"%>
	<!-- 删除时确认窗口 -->
	<script src="static/ace/js/bootbox.js"></script>
	<!-- ace scripts -->
	<script src="static/ace/js/ace/ace.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<script type="text/javascript">
	$(top.hangge());//关闭加载状态
	//检索
	function tosearch(){
		top.jzts();
		$("#Form").submit();
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
	
	
	function doCheck(appNo){
		top.jzts();
		$.ajax({
			type: "POST",
			url: '<%=basePath%>doCheck.do?appNo='+appNo,
			dataType:'json',
			//beforeSend: validateData,
			cache: false,
			success: function(data){
				showDialog(data.result);
				 top.Dialog.close();
			}
		});
	}
	

	</script>

</body>
</html>