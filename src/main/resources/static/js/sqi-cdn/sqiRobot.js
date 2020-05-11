$('document').ready(function(){
    $.fn.extend({
        "sqiRobot": function (option) {
            if(option.hostName != null && option.hostName != ""){
                hostName = option.hostName;
            };
            var target = this;
            if(typeof(option.userNick)=='undefined' || typeof(option.userCode)=='undefined' || typeof(option.chatBu)=='undefined' || typeof(option.productType)=='undefined')
            {
                console.log(option);
                console.log("参数不正确，机器人插件初始化失败，请参考文档：https://lark.alipay.com/aone527698/doc/chapr8");
            }else if(typeof(option.bucId)=='undefined' && typeof(option.userCode)!='undefined' ){
                console.log("buc");
                $.ajax({
                    url:"//"+hostName+"/robot/api/findIdByCode.json",
                    data:{
                        code:option.userCode,
                        appKey:option.appKey
                    },
                    jsonp:"callback",
                    success:function(d){
                        if(d.code == 200){
                            option.bucId = d.fields;
                            initScript(target,option);
                        }
                        if(typeof(option.bucId) == "undefined" || option.bucId == ""){
                            console.log(option);
                            console.log("参数不正确，机器人插件初始化失败，请参考文档：https://lark.alipay.com/aone527698/doc/chapr8");
                        }
                    }
                });
            }else {
                initScript(target,option);
            }
        }
    });
    //加载script
    function initScript(target,option) {
        var asrUrl = "//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/js/ASRClient.js?ts=" + Date.parse(new Date()) ;//调用远程的ASR
        // let asrUrl = "/robot/assets/js/chatBox/ASRClient.js?ts=" + Date.parse(new Date()) ;//调用本地的ASR
        var showDownUrl ="//g.alicdn.com/sqi-project/si-robot/js/showdown.min.js?ts="+Date.parse(new Date()); // markdown转html
        var html2canvasUrl ="//g.alicdn.com/sqi-project/si-robot/js/html2canvas.js?ts="+Date.parse(new Date()); // 截屏插件
        var sceneBasedCustomerServiceUrl = "//g.alicdn.com/sqi-project/si-robot/js/chatBox/SceneBasedCustomerService.js?ts="+Date.parse(new Date());
        var RFHTMLParserUrl = "//g.alicdn.com/sqi-project/si-robot/js/RFHTMLParser.js?ts="+Date.parse(new Date());
        $.getScript(showDownUrl).done(function() {
            //需要语音功能
            if(typeof(option.audio)=='undefined' || option.audio=='true' || option.audio==true){
                $.getScript(asrUrl).done(function() {
                    console.log('get ASRClient.js success . ');
                });
            }
            if(typeof(option.screenShot)=='undefined' || option.screenShot == 'true' || option.screenShot == true || option.productType =='chatbot'){
                $.getScript(html2canvasUrl).done(function() {
                    console.log('get html2canvas.js success . ');
                });
            }
            if(typeof(option.sceneBasedService) != undefined && (option.sceneBasedService == "true" || option.sceneBasedService == true)) {
                $.getScript(sceneBasedCustomerServiceUrl).done(function(){
                    console.log('get SceneBasedCustomerService.js success. ');
                    window.RFSceneBasedCustomerServiceFunction = new SceneBasedCustomerService();
                });
            }
            $.getScript("//g.alicdn.com/mcms/robot-factory/0.0.1/robot-factory_sqiRobot.json").done(function(){
                console.log("mcms done");
                $.getScript(RFHTMLParserUrl).done(function(){
                  console.log('get RFHTMLParser done');
                  RobotObj.init(target,option);
                });
            });
        });
    }
    //定义ASR的相关配置
    var ASRconfig = {
        config : {
            "url" : "wss://sqi.alibaba-inc.com/asr?app=et&token=xxx" ,
            "constraints" : {
                "audio": true,
                "video": false
            }
        }
    };
    //如果是localhost的访问，允许使用ws
    if(document.domain == 'localhost'){
        ASRconfig.config.url = "ws://localhost:8082/asr?app=et&token=xxx"
    }
    var is_recording = false ;
    var is_manual=false; //机器人还是人工的状态
    var manual_info=false; //是否是获取人工客服名称，manual_info=false 则显示人工客服***为您服务，需要输入一段话来描述问题，与不满意直接转人工不同
    var chatBu = '';  //当前产品所在的bu
    var robotProductType = ''; //当前产品名称
    var robotUserId = ''; 	//当前使用机器人的用户bucId
    var robotUserNick = ''; //用户的昵称
    var robotUserCode = ''; //用户的工号
    var defineDom='sqi_robot';
    var hostName = "sqi.alibaba-inc.com"; //所有请求的服务端地址
    var defineTitle="答疑机器人";
    var robotNick ="机器人";
    var placeholder = "请描述遇到的问题，command+v可粘贴图片,如需人工请输入“人工”";
    var converter = ""; // 初始化showdown方法，markdown转html
    var robotParentShow = false; //默认是否展开对话框
    var markdown = true; // 将html转换成markdown，默认是转换
    var customer = true; // 开启人工客服功能
    var screenshot = false; //关闭一键截屏功能
    var sceneBasedService = false; // 是否启用基于现场信息的答疑，默认不启用
    var RFHTMLParserFunction = null;
    var lang = "zh_CN";
    var lexicon = "";
    var answerTemplate = "<ul id='answerTemplate' class='demo-template'><li class='chat-item other' ><img data='$answerImg' alt='' class='img-circle chat-avatar'>" +
        "<time class='block fs-12 text-muted'>$currentTime $userNick</time><div class='chat-bubble'><div class='chat-text'>$chatText</div></div></li></ul>";
    var questionTemplate =  "<ul id='questionTemplate' class='demo-template'><li class='chat-item self'><img data='https://work.alibaba-inc.com/photo/$userCode.50x50.jpg' alt='' class='img-circle chat-avatar'>"+
        "<time class='block fs-12 text-muted'>$userNick $currentTime</time><div class='chat-bubble'><div class='chat-text'>$chatText</div></div></li></ul>";
    var RobotObj = {
        init: function(target,option){
            var self = this;
            self.initParam(option);
            self.setRobotHtml(target,option);//初始化机器人窗口内容
            apushConnect();//apush连接websocket，接收人工客服的消息
            getHotQuestion();//热门问题初始化
            // getHistoryDateList();//聊天记录相关
            bindEvent(); //绑定各种事件
            getNoReadResponse();//获取未读记录数，显示在机器人小图标上
            setEmoji();  //表情内容添加及事件绑定
            // getUserStatus(0);//获取当前用户的状态，是人工还是机器人回复
            if(markdown && typeof(showdown) !="undefined" && showdown !=""){
                converter = new showdown.Converter(); // 初始化
            }
            if (RFHTMLParserFunction == null && typeof(RFHTMLParser) == "function") {
                RFHTMLParserFunction = new RFHTMLParser();
            }
        },
        initParam:function(option){
            chatBu = option.chatBu;
            robotProductType = option.productType;
            robotUserId = option.bucId;
            robotUserNick = option.userNick;
            robotUserCode = option.userCode;
            lexicon = window["robot-factory_sqiRobot"][lang];
            if(option.robotParentShow != null) {
                robotParentShow = option.robotParentShow;
            }
            if(option.defineDom !=null  && option.defineDom.length>0){
                defineDom =  option.defineDom;
                option.style = "display:none"
            }
            if(option.defineTitle != null && option.defineTitle.length >0){
                defineTitle = option.defineTitle;
            }else{
                defineTitle=lexicon["Q & A"]+lexicon["robot"];
            }
            if(option.robotNick != null && option.robotNick.length >0) {
                robotNick = option.robotNick;
            }else{
                robotNick =lexicon["robot"];
            }
            if(option.markdown != null && typeof(option.markdown) != "undefined"){
                markdown = option.markdown;
            }
            if(option.customer != null && typeof(option.customer) != "undefined"){
                customer = option.customer;
            }
            if(typeof(option.screenShot)=='undefined' || option.screenShot == 'true' || option.screenShot == true || option.productType =='chatbot'){
                screenshot = true;
            }
            if(option.lang != null && typeof(option.lang) != "undefined"){
                lang = option.lang;
            }
            if(typeof(option.sceneBasedService) != undefined && (option.sceneBasedService == "true" || option.sceneBasedService == true)) {
                sceneBasedService = true;
            }
        },
        //生成机器人窗口的html内容
        setRobotHtml: function(target,option){
            //判断是否可支持语音，用户是否需要支持语音，增加语音的图标
            var audioLink = "";
            var ishttps = 'https:' == document.location.protocol ? true: false;
            var isChrome = navigator && navigator.userAgent && navigator.userAgent.indexOf('Chrome') > 0 ;
            //判断是不是https或者localhost 并且必须是Chrome
            if(isChrome && (ishttps || document.domain=='localhost') && (option.audio=='true' || option.audio==true)){//只有https协议支持语音功能
                audioLink = "<a data-toggle='tooltip' id='startRobotRecord'  data-placement='top' title='语音输入' class='history-icon' > <img style='width:17px;' id='startRobotRecordImg' src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/record.png'></a>"
            }
            var fileUpload ="";
            if(option.fileUpload){
                fileUpload = "<div data-toggle='tooltip' data-placement='top'  title='上传文件' class='history-icon fileinput-button' > <img style='width:17px;' src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/fileUpload.png'><input type='file' id='uploadRobotFile' ></div>";
            }
            var screenShot = "";
            if(screenshot){
                screenShot = "<a data-toggle='tooltip'  data-placement='top' title='一键截屏' class='history-icon screenShot-button'> <img style='width:17px;' src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/photo1.png'></a>"
            }
            var initHtml =
                "<div class='robot-parent'>"+
                "<div class='robot-modal' >"+
                "<p class='chat-title'>" +
                "<i data-toggle='tooltip' data-placement='top' title='聊天记录' class='J_chat_convert' id='history' ><img id='chat-img' src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/history1.png'></i>" +
                "<span>"+defineTitle+"</span><i class='J_hide_robot'><img src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/close.png' style='width: 20px;'></i>" +
                "</p>"+
                "<div class='chat-main'>"+
                "<div class='chat-panel'>"+
                "<div class='chat-note'>" +
                "<ul class='noteTemplate'>"+
                "<li class='chat-item other'><span class='triangle'></span><img src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/robot.png' alt='' class='img-circle chat-avatar'>"+
                "<time class='block fs-12 text-muted'>"+robotNick+"</time>"+
                "<div class='chat-bubble'>"+
                "<div class='note-content chat-text'></div>"+
                "</div>"+
                "</li>"+
                "</ul>"+
                "</div>"+
                "<div class='chat-top'>" +
                "<div class='hot-div'>"+
                "<ul class='noteTemplate'>"+
                "<li class='chat-item other'><span class='triangle'></span><img src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/robot.png' alt='' class='img-circle chat-avatar'>"+
                "<time class='block fs-12 text-muted'>"+robotNick+"</time>"+
                "<div class='chat-bubble'>"+
                "<div class='chat-text'>" +
                "<div class='title'><i class='fa fa-question-circle-o'></i>"+lexicon["Top issues"]+"</div>"+
                "<ul class='hot-ul'></ul>" +
                "</div>"+
                "</div>"+
                "</li>"+
                "</ul>"+
                "</div>"+
                "</div>"+
                "<ul class='chat-ul'></ul>"+
                "</div>"+
                "<div class='form-group has-feedback'>"+
                "<div class='robot-extend' >"+
                "<a data-toggle='tooltip' id='chatBoxEmoji'  data-placement='top' title='表情' class='history-icon'> <img style='width:17px;' src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/emoji.png'></a>"+
                fileUpload+
                "<div class='mod-plugin-smiley' id='chatEmojiPanel'>"+
                "<div class='J_Wrapper'>"+
                "<div class='arrow-bottom'></div>"+
                "<div class='J_Scroller scroller' id='emojiPanel'>"+
                "<ul class='smiley-ul-sqi' id='emojiSqi'></ul>"+
                "</div>"+
                "</div>"+
                "</div>"+
                audioLink+
                screenShot+
                "</div>"+
                "<div class='input-group'  style='margin:0px 0px 10px 0px;'>"+
                "<input type='textarea' id='chatInput' style='width:90%;border-color:rgba(120,130,140,0);margin: 10px 15px;'  title='请您描述遇到的问题'>"+
                "</div>"+
                "</div>"+
                "<a class='over-link'>"+lexicon["End of manual service"]+"</a>"+
                "<a class='sendDingding'>"+lexicon["Urge"]+"</a>" +
                "</div>"+
                "<div class='chat-history'>" +
                "<div class='chat-history-left'><ul id='sqiChatDateUl'></ul></div>" +
                "<div class='left-history-control history-mini-right'><span></span></div>" +
                "<div class='chat-history-right'><ul class='chat-ul' id='sqiChatHistoryUl'><span style='position: absolute;top: 280px;left: 40%;'>暂无聊天记录</span></ul></div>" +
                "</div>"+
                "</div>"+
                "</div>"+
                "<a data-toggle='tooltip' data-placement='left' title='"+defineTitle+"' id='sqi_robot' style='"+option.robotStyle+"'>" +
                "<img src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/icon_robot.png' ><span class='robot-info'>0</span>" +
                "</a>"+
                '<div id="userChatHistoryModal" tabindex="-1" role="dialog">'+
                '<div class="modal-dialog" style="width:830px;">'+
                '<div class="modal-content">'+
                '<div class="modal-header">'+
                '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><i><img src="//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/close.png" style="width: 20px;"></i></button>'+
                '</div>'+
                '<div class="modal-body" style="text-align:center">'+
                "<img id='imgLarge'>"+
                '</div>'+
                '</div>'+
                '</div>'+
                '</div>'+
                '<div id="screenShotModal" tabindex="-1" role="dialog">'+
                '<div class="modal-dialog" style="width:830px;">'+
                '<div class="modal-content">'+
                '<div class="modal-header">'+
                '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><i><img src="//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/close.png" style="width: 20px;"></i></button>'+
                '</div>'+
                '<div class="modal-body screen-body" style="text-align:center">'+
                '<div style=" text-align: right;padding: 10px 10px 0px 0px;">' +
                '<div class="pen-wrapper">' +
                '<button id="rovoked" class="pen-btn rovoked-btn"><img src="//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/revoked.png">撤销</button>'+
                '<button id="pencil" class="pen-btn pencil-btn active-btn"><img src="//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/pencil.png">画笔</button>'+
                '</div>' +
                '</div>'+
                '<div class="canvas"></div>'+
                '</div>'+
                '<div class="modal-footer" style="text-align:center">'+
                '<button type="button" id="sendScreenImg">发送截屏</button>'+
                '</div>'+
                '</div>'+
                '</div>'+
                '</div>'
            $(target).html(initHtml);
            //光标自动focus在输入框
            document.getElementById('chatInput').focus();
            checkSyetem();//检查系统mac还是windows，展现不同的输入框提示
            var element = document.getElementById(defineDom);
            var robotElement = $(element).find(".robot-info");
            if(typeof robotElement == "undefined" || $(robotElement).length <=0) {
                var top = $(element)[0].offsetTop-5;
                var left = $(element)[0].offsetLeft+$(element)[0].offsetWidth+1;
                $(element).append("<span class='robot-info'>0</span>");
                $(element).find(".robot-info").css("left",left+"px");
                $(element).find(".robot-info").css("right","auto");
                $(element).find(".robot-info").css("top",top+"px");
                $(element).find(".robot-info").css("text-align","center");
                $(element).find(".robot-info").css("color","#fff");
                $(element).find(".robot-info").css("padding", "2px 3px");
            }
            if(typeof($(element).tooltip)=='function'){
                //提示功能
                $(element).tooltip();
                $('.robot-extend .history-icon').tooltip();
            }
            //启用拖拽功能
            if(typeof(option.draggable)!='undefined' && option.draggable==true){
                dragRobot();
            }
            var color = $("#sqi_robot").css("background");
            $(".chat-title").css("background", color);
            $("#userChatHistoryModal .modal-header").css("background", color);
            $("#screenShotModal .modal-header").css("background", color);
            $('#sendScreenImg').css("background",color)
            $("#sqi_robot").css("background","none");
            if(option.robotStyle != null && option.robotStyle.length >0 && $("#sqi_robot").css("display") != "none") {
                var width = $('.robot-parent').css("width").replace("px","");
                var left = $("#sqi_robot")[0].offsetLeft- parseInt(width);
                $('.robot-parent').css("left", left + "px");
            }
        }
    };

    //每次有新的消息，或者发送内容后，滚动条滚动到窗口最底部
    function setHeight(time){
        setTimeout("var height = $('.robot-modal .chat-panel')[0].scrollHeight;$('.robot-modal .chat-panel').scrollTop(height);",time);
    }
    //图片放大展现
    function showImg(){
        $('.chat-text img').click(function(e){
            if(!$(e.target).hasClass('key-down')) {
                var src = $(e.target).attr('src');
                $('#imgLarge').attr('src', src);
                $('#userChatHistoryModal').show();
            }
        });
    }

    //不同的系统展现不同的文案信息
    function checkSyetem(){
        var sUserAgent = navigator.userAgent;
        var isWin = (navigator.platform == "Win32") || (navigator.platform == "Windows");
        var isMac = (navigator.platform == "Mac68K") || (navigator.platform == "MacPPC") || (navigator.platform == "Macintosh") || (navigator.platform == "MacIntel");
        if(isMac)
        {
            $('#chatInput').attr('placeholder',lexicon["Please describe the problem"]+"'command+v'"+lexicon["paste picture"]);
        }
        else
        {
            $('#chatInput').attr('placeholder',lexicon["Please describe the problem"]+"'ctrl+v'"+lexicon["paste picture"]);
        }
    }

    //检查内容是否html代码
    function checkHtml(htmlStr) {
        var  reg = /<[^>]+>/g;
        return reg.test(htmlStr);
    }

    //检查内容是否html代码
    function checkHttp(httpStr) {
        var  reg = /(http:\/\/|https:\/\/)((\w|=|\?|\.|\/|&|-|:)+)/g;
        return reg.test(httpStr);
    }
    // encode
    function unescapeHTML(str){
        return str.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&amp;/g, "&").replace(/&quot;/g, '"').replace(/&apos;/g, "'");
    }
    //自动转换用户输入的url到可点击的link
    String.prototype.httpHtml = function(){
        var reg = /(http:\/\/|https:\/\/)((\w|=|\?|\.|\/|&|-|:)+)/g;
        return this.replace(reg, '<a target="_blank" href="$1$2">$1$2</a>');
    };
    // 基于现场信息答疑——不发送
    function dropSceneDataFunction(element) {
        $(element).closest(".chat-item").css("display", "none");
        if (!window.RFSceneBasedCustomerServiceFunction) {
            window.RFSceneBasedCustomerServiceFunction = new SceneBasedCustomerService();
        }
        window.RFSceneBasedCustomerServiceFunction.removeSceneData();
    }
    function sceneDataTriggerFunction() {
        if (!window.RFSceneBasedCustomerServiceFunction) {
            window.RFSceneBasedCustomerServiceFunction = new SceneBasedCustomerService();
        }
        if (RFHTMLParserFunction == null && typeof(RFHTMLParser) == "function") {
            RFHTMLParserFunction = new RFHTMLParser();
        }
        if(window.RFSceneBasedCustomerServiceFunction && RFHTMLParserFunction) {
            var sceneBasedFunction = window.RFSceneBasedCustomerServiceFunction;
            // var json = {
            //     msgType: "simpleActionCard",
            //     img: [
            //         {
            //             src: "https://gw.alipayobjects.com/zos/rmsportal/NmZnQHNEdpOkZbmbDrCW.png"
            //         }
            //     ],
            //     link: [
            //         {
            //             url: "http://www.baidu.com",
            //             alt: "百度",
            //         }
            //     ],
            //     text: "今天天气真不错",
            //     btnsType: "sendTrigger",
            // };
            // sceneBasedFunction.writeSceneData(JSON.stringify(json));
            var sceneData = sceneBasedFunction.readSceneData();
            if (sceneData) {
                var question = $(questionTemplate).html().replace("<img", "<span class='triangle'></span><img").replace('data="https://work.alibaba-inc.com/photo/$userCode.50x50.jpg"', "src='https://work.alibaba-inc.com/photo/" + robotUserCode + ".50x50.jpg'").replace('$userNick', getNowFormatDate()).replace('$currentTime', robotUserNick);
                var sceneDataHtml = RFHTMLParserFunction.parseSimpleActionCard(sceneData);
                question = question.replace('$chatText', sceneDataHtml);
                $('.chat-ul').append(question);
            }

        }
    }

    // 根据传入的url解析参数，获取指定key的值
    function parseParamsFromUrl(url, key) {
        var regex = new RegExp(key+"=([^&]*)","i");
        return url.match(regex)[1];
    }

    /***机器人事件绑定****/
    function bindEvent(){
        //机器人窗口展开
        var element = document.getElementById(defineDom);
        $(element).click(function(e){
            if($('.robot-parent')[0].style.display == "" || $('.robot-parent')[0].style.display == "none"){
                $('.robot-parent').css('display', 'block');
                $(element).find('.robot-info').css('display', 'none');
                document.getElementById('chatInput').focus();
                setHeight(100);
                showImg();
                setReadStatus(); //更新所有记录已读
            }else {
                $('.robot-parent').css('display', 'none');
            }
        });

        // added by qianyang 网页版机器人支持消息串联
        $("#robotContainer").on("click", "a[href*='dtmd://dingtalkclient/sendMessage?']", function(e){
            var href = $(e.target).attr("href");
            var content = decodeURI(parseParamsFromUrl(href, "content"));
            setChatResponse(content, 0);
        });

        // add by qianyang 监听chatInput获取焦点事件
        if(sceneBasedService) {
            $('#chatInput').click(function () {
                sceneDataTriggerFunction();
                setHeight();
            });
            $("#robotContainer").on("click", ".simpleActionCardYesButton", function(e){
                var data = JSON.parse($(e.target).attr("data").replace(/'/g, '"'));
                var addContentTypeData = data;
                addContentTypeData.contentType = "body"; // 只获取现场信息内容，去掉发送/不发送按钮
                var question = $(questionTemplate).html().replace("<img", "<span class='triangle'></span><img").replace('data="https://work.alibaba-inc.com/photo/$userCode.50x50.jpg"', "src='https://work.alibaba-inc.com/photo/" + robotUserCode + ".50x50.jpg'").replace('$userNick', getNowFormatDate()).replace('$currentTime', robotUserNick);
                var sceneDataHtml = RFHTMLParserFunction.parseSimpleActionCard(addContentTypeData);
                question = question.replace('$chatText', sceneDataHtml);
                dropSceneDataFunction(e.target);
                $('.chat-ul').append(question);
                setChatResponse(JSON.stringify(data), 5);
            });

            $("#robotContainer").on("click", ".simpleActionCardNoButton", function(e){
                dropSceneDataFunction(e.target);
            });
        }
        if(robotParentShow){
            $(element).trigger("click");
            $('.robot-parent').css('left', '0px');
        }
        $('.J_chat_convert').click(function(e){
            var target = $(e.target);
            if($(target).is('img')) {
                target = $(e.target).parent();
            }
            var id = $(target).attr('id');
            if(id === "history") {
                $('.chat-main').css('display', 'none');
                $('.chat-history').css('display', 'block');
                $(target).attr("id","main");
                $(target).attr("title","客服界面");
                $('#chat-img')[0].src= "//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/customer.png";
            }else{
                $('.chat-main').css('display','block');
                $('.chat-history').css('display','none');
                $(target).attr("id","history");
                $(target).attr("title","聊天记录");
                $('#chat-img')[0].src= "//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/history1.png";
            }
        });
        $('#userChatHistoryModal').click(function (e) {
            if($(e.target).attr('id') == "userChatHistoryModal"){
                $('#userChatHistoryModal').hide();
            }
        });
        $('#userChatHistoryModal .close').click(function(e){
            $('#userChatHistoryModal').hide();
        });
        $('#screenShotModal').click(function (e) {
            if($(e.target).attr('id') == "screenShotModal"){
                $('#screenShotModal').hide();
            }
        });
        $('#screenShotModal .close').click(function(e){
            $('#screenShotModal').hide();
        });
        //催一下
        $('.sendDingding').click(function(e){
            $.ajax({
                url:"//"+hostName+"/robot/api/SendDingDingMessage.json",
                dataType:"jsonp",
                data:{
                    origin_user:robotUserId,
                    chatBu:chatBu,
                    product_type:robotProductType,
                    chatUserNick:robotUserNick
                },
                jsonp:"callback",
                success:function(d){
                    var newHtml ="<li class='center-info'><div class='li-message'>"+lexicon["Urge success"]+"</div></li>";
                    $('.chat-ul').append(newHtml);
                    setHeight(100);
                    showImg();
                    $('.sendDingding').css('display','none');
                }
            });
        });
        //结束服务
        $('.over-link').click(function(e){
            changeManualStatus(false);
            $.ajax({
                url:"//"+hostName+"/robot/EndManualChatSession.json",
                data:{
                    product_type:robotProductType,
                    chatBu:chatBu,
                    chatUser:robotUserId
                },
                dataType:"jsonp",
                jsonp:"callback",
                success:function(d){
                }
            });
        });
        //粘贴上传图片
        $('#chatInput').bind('paste',function(e){
            var pastedText = undefined;
            var items = (e.clipboardData || e.originalEvent.clipboardData).items;
            for (index in items) {
                var item = items[index];
                if (item.kind === 'file') {
                    var blob = item.getAsFile();
                    var reader = new FileReader();
                    reader.onload = function(event) {
                        var baseImg=event.target.result;
                        $.ajax({
                            url : '//'+hostName+'/robot/chatBox/uploadFilesBase64.json',
                            type : "POST",
                            data : {imgContent:baseImg},
                            crossDomain:true,
                            contentType:"application/x-www-form-urlencoded",
                            success:function(dataObj){
                                var imgPath = dataObj.url.split("filePath=")[1].split("&")[0];
                                setChatResponse(imgPath,1);
                            }
                        });
                    };
                    reader.readAsDataURL(blob);

                }
            }
        });
        // 点击截屏按钮
        $('.screenShot-button').click(function (e) {
            $('.robot-parent').css('display','none');
            $('.canvas').html("");
            html2canvas(document.body,{
                useCORS: true
            }).then(function (canvas) {
                $('.canvas').html("");
                canvas.id = 'screenshotCanvas';
                $('.canvas').append(canvas);
                $("#screenshotCanvas").css("width","780px");
                $('#screenShotModal').show();
                $('.rovoked-btn').addClass("disabled-btn");
                bindCanvasEvent();
                $('.robot-parent').css('display','block');
            });
        });
        //点击发送截屏按钮
        $('#sendScreenImg').click(function (e) {
            var baseImg = document.getElementById("screenshotCanvas").toDataURL();
            $.ajax({
                url : '//'+hostName+'/robot/chatBox/uploadFilesBase64.json',
                type : "POST",
                data : {imgContent:baseImg},
                crossDomain:true,
                contentType:"application/x-www-form-urlencoded",
                success:function(dataObj){
                    var imgPath = dataObj.url.split("filePath=")[1].split("&")[0];
                    setChatResponse(imgPath,1);
                    $('#screenShotModal').hide();
                }
            });
        });
        // 为canvas元素onmousedown和onmouseup事件
        var bindCanvasEvent = function(){
            var canvas = document.getElementById("screenshotCanvas");
            var context = canvas.getContext("2d");
            context.strokeStyle='#ef0d2d';
            context.lineWidth = 3;
            var flag = false;
            var lastImg = [];
            $(canvas).mousedown(function (e) {
                flag = true;
                lastImg.push(context.getImageData(0,0,context.canvas.width,context.canvas.height));
                $('.rovoked-btn').removeClass("disabled-btn");
            }).mouseup(function () {
                flag = false;
            }).mousemove(function (e) {
                draw(e);
            });
            function draw(e) {
                if(flag){
                    context.lineTo(parseInt(e.offsetX*1.85), parseInt(e.offsetY*1.85));
                    context.stroke();
                }else{
                    context.beginPath();
                }
            }
            $('#rovoked').click(function (e) {
                if(lastImg.length >0) {
                    context.putImageData(lastImg.pop(), 0, 0);
                }else{
                    $('.rovoked-btn').addClass("disabled-btn");
                }
            })
        };
        //点击发送按钮
        $('.J_chatSend').click(function(e){
            var query = $('#chatInput').val();
            setChatResponse(query,0);
        })
        //回车发送问题
        $('#chatInput').keydown(function(e){
            if(e.keyCode==13){
                if(is_recording && asrClient){
                    is_recording = false ;
                    $('#startRobotRecordImg').attr('src','//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/record.png') ;
                    asrClient.end() ;
                    setTimeout(function(){
                        //延迟一秒返回
                        asrClient.stop() ;
                        asrClient = null ;
                        var query = $(e.target).val();
                        setChatResponse(query,0);
                    } , 200)
                }else{
                    var query = $(e.target).val();
                    setChatResponse(query,0);
                }
            }
        });
        //隐藏机器人窗口
        $('.J_hide_robot').click(function(e){
            $('.robot-parent').css('display','none');
        });
        //上传文件
        $('#uploadRobotFile').on('change', function(e) {
            var imgFile = document.getElementById("uploadRobotFile").files[0];
            var formData=new FormData();
            formData.append("imgFile",imgFile);
            formData.append("fileType","file");
            formData.append('parentDir',"robotUploadFile");
            $.ajax({
                url : '//'+hostName+'/robot/chatBox/uploadFiles.json',
                type : "POST",
                data : formData,
                dataType:"text",
                processData : false,
                contentType : false,
                success:function(data){
                    var dataObj=eval("("+data+")");
                    if(typeof(dataObj.error)!='undefined' && dataObj.error==1){
                        alert("非法文件类型，不支持.js,.jsp,.exe文件上传");
                    }
                    else
                        setChatResponse(dataObj.url,4);
                }
            });
        });
        // 展示隐藏左侧
        $('.left-history-control').click(function (e) {
            if($('.left-history-control').hasClass("history-mini-left")){
                $('.chat-history-left').css("display","none");
                $('.chat-history-right').css("width","100%");
                $('.left-history-control').removeClass("history-mini-left");
                $('.left-history-control').addClass("history-mini-right");
            }else{
                $('.chat-history-left').css("display","block");
                $('.chat-history-right').css("width","70%");
                $('.left-history-control').removeClass("history-mini-right");
                $('.left-history-control').addClass("history-mini-left");
            }
        });
        // 语音输入的内容
        var asrClient = null ;
        $('#startRobotRecord').click(function(e){
            //如果不是正在输入，则创建一个客户端进行输入
            if(!is_recording){
                asrClient = new ASRClient(ASRconfig  , function(msg){
                    var d = msg.word ;
                    if(d != '' && d != null){
                        var input = $('#chatInput').val();
                        input += d;
                        $('#chatInput').val(input);
                        document.getElementById('chatInput').focus();
                        $('.sendDingding').html('确认语音内容无误后，请回车提交');
                        $('.sendDingding').css('display','block');
                        var urge = lexicon["Urge"];
                        setTimeout("$('.sendDingding').css('display','none');$('.sendDingding').html(urge);",
                            5000);
                        $('#chatInput').attr('placeholder',lexicon["Please describe the problem"]+"'command+v'"+lexicon["paste picture"]);
                    }else{
                        $('#chatInput').val('');
                        $('#chatInput').attr('placeholder','没有识别到文字，请描述遇到的问题，command+v可粘贴图片');
                    }
                    $('#chatInput').attr('placeholder',lexicon["Please describe the problem"]+"'command+v'"+lexicon["paste picture"]);
                } , function(error){
                    $('#chatInput').attr('placeholder','没有识别到文字，请描述遇到的问题，command+v可粘贴图片');
                    $('#startRobotRecordImg').attr('src','//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/record.png') ;
                }) ;
                asrClient.start() ;
                $('#chatInput').attr('placeholder','请允许麦克风并说话...');
                is_recording = true ;
                $('#startRobotRecordImg').attr('src','//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/recording.png')
            }
            //否则结束输入
            else {
                if(is_recording && asrClient){
                    is_recording = false ;
                    asrClient.end() ;
                    setTimeout(function(){
                        asrClient.stop() ;
                        asrClient = null ;
                    },200)
                }
                $('#startRobotRecordImg').attr('src','//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/record.png') ;
            }
        });
    }

    //获取未读的记录数量，一进入页面立即请求
    function getNoReadResponse(){
        $.ajax({
            url:"//"+hostName+"/robot/chatBox/getCountNoRead.json",
            data:{
                chatBu:chatBu,
                chatUser:robotUserId,
                productType:robotProductType
            },
            dataType:"jsonp",
            jsonp:"callback",
            success:function(d){
                var element = document.getElementById(defineDom);
                if(d>0){
                    $(element).find('.robot-info').html(d);
                    $(element).find('.robot-info').css('display','block');
                }
                else{
                    $(element).find('.robot-info').css('display','none');
                }
            }
        });
    }



    //拖拽功能
    function dragRobot(){
        var element = document.getElementById(defineDom);
        if(typeof($(element).draggable)=='function'){
            $(".robot-parent").draggable({
                start: function () {
                    $(".robot-parent").css("right", "auto");
                },
                stop:function(){
                    var top = $(".robot-parent").css("top");
                    var contentTop=top.substr(0,top.indexOf("px"));
                    if(contentTop <0){
                      $(".robot-parent").css("top",0);
                    }
                },
                cursor: "move", handle: ".chat-title" ,containment: 'body'
            });
            $('#sqi_robot').draggable({cursor: "move", containment: 'body'});
        }
        else{
            var dragUrl ="//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/js/robotDrag.js?_t=" + Date.parse(new Date());
            $.getScript(dragUrl).done(function() {
                console.log("get robotDrag.js success",window.dataworksHead);
                $(".robot-parent").draggable({
                    start: function () {
                        $(".robot-parent").css("right", "auto");
                    },
                    stop:function(){
                        var top = $(".robot-parent").css("top");
                        var contentTop=top.substr(0,top.indexOf("px"));
                        if(contentTop <0){
                          $(".robot-parent").css("top",0);
                        }
                    },
                    cursor: "move", handle: ".chat-title" ,containment: 'body'
                });
                $('#sqi_robot').draggable({cursor: "move", containment: 'body'});
            });
        }
    }

    /* ******************** */
    /* 获取热门问题  */
    /* ******************** */
    function getHotQuestion(){
        $.ajax({
            url:"//"+hostName+"/robot/hotQuestionList.json",
            dataType:"jsonp",
            data:{
                productType:robotProductType,
                chatBu:chatBu
            },
            jsonp:"callback",
            success:function(d){
                var hot = d.hotQuestion;
                if(hot.length==0)//没有配置热门问题
                    $('.hot-div').css('display','none');
                for(var i=0;i<hot.length;i++){
                    var newLi = "<li class='J_hot'><a ><span>"+hot[i]+"</span><img class='key-down' src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/chat-key.png'></a></li>";
                    $('.hot-ul').append(newLi);
                }
                var note = $('.note-content');
                if(d.appNote.length >0) {
                    $('.chat-note').css("display","block");
                    note.append(d.appNote);
                    var img = note.find('img');
                    for (var i = 0; i < img.length; i++) {
                        if (typeof($(img[i]).attr("class")) == "undefined") {
                            $(img[i]).attr("class", "aligncenter")
                        }
                    }
                }else{
                    $('.chat-note').css("display","none");
                }
                bindHotClick();
                setHeight(100);
                showImg();
            }
        });
    }

    function bindHotClick() {
        $('.J_hot').off('click');
        $('.J_hot').on('click',function(e){
            var query="";
            if(e.target.nodeName.toLowerCase()=="span")
            {
                query = $(e.target).html();
            }
            else if(e.target.nodeName.toLowerCase()=="img"){
                query = $(e.target).prev('span').html();
            }
            else
            {
                query = $(e.target).find('span').html();
            }
            setChatResponse(query,0);
        });
    }

    /* **********************************/
    /* apush进行websocket连接 接收人工客服消息 */
    /* **********************************/
    function apushConnect(){
        $.getScript("//g.alicdn.com/tb/apush/0.0.15/socketio.js").done(function() {
            $.getScript("//g.alicdn.com/tb/apush/0.0.15/apush.js").done(function() {
                var locationHost = location.hostname;
                var dailyOnlineDomain = window.dailyOnlineDomain;
                var env = "pro";
                if(locationHost.indexOf("localhost")!=-1 || locationHost.indexOf("10")!=-1 || dailyOnlineDomain==locationHost){
                    env="dev";
                }
                //本地不连接apush
                if(env!="dev"){
                    $.ajax({
                        url:"//"+hostName+"/robot/chatBox/getApushUrl.json",
                        dataType:"jsonp",
                        data:{
                            productType:robotProductType,
                            chatBu:chatBu,
                            chatUser:robotUserId,
                            env:env
                        },
                        jsonp:"callback",
                        success:function(apush_url){
                            var element = document.getElementById(defineDom);
                            var ishttps = 'https:' == document.location.protocol ? true: false;
                            if(ishttps){
                                apush_url = apush_url.replace("//hzapush.aliexpress.com:80","//hzapush.aliexpress.com");
                            }
                            var apushClient = Apush.createClient(apush_url,function(res){
                                if(typeof(res.arg)!='undefined' && res.msgType=="ManualStatus"){
                                    var dataObj=eval("("+res.arg+")");
                                    if(dataObj.manualStatus && !is_manual){
                                        changeManualStatus(true);
                                    }
                                    if(!dataObj.manualStatus && is_manual){
                                        changeManualStatus(false);
                                    }
                                }
                                else if(typeof(res.arg)!='undefined' && res.msgType=="NOREAD"){
                                    var dataObj=eval("("+res.arg+")");
                                    if(dataObj.noRead>0 && !$('.robot-parent').is(':visible')){
                                        $(element).find('.robot-info').html(dataObj.noRead);
                                        $(element).find('.robot-info').css('display','block');
                                    }
                                    else if(dataObj.noRead>0){
                                        $(element).find('.robot-info').css('display','none');
                                        setReadStatus();
                                    }
                                    else{
                                        $(element).find('.robot-info').css('display','none');
                                    }
                                    //人工的回答消息获取展现，替代轮训

                                    var nick= dataObj.fromUserNick;
                                    var content =dataObj.text;
                                    content = unescapeHTML(content);
                                    if(content!=""){
                                        var systemFlag = dataObj.systemFlag;
                                        if(systemFlag==1){
                                            //系统消息，客服或用户主动结束了人工服务
                                            changeManualStatus(false);

                                            var chatHtml ="<li class='center-info'><div class='li-message'>"+content+"</div></li>";
                                            $('.chat-ul').append(chatHtml);
                                        }
                                        else{
                                            var currentTime = dataObj.date;
                                            var userImg = "https://work.alibaba-inc.com/photo/"+dataObj.fromUserCode+".50x50.jpg";
                                            var answer=$(answerTemplate).html().replace("<img","<span class='triangle'></span><img")
                                                .replace("$userNick", currentTime)
                                                .replace('data="$answerImg"',"src='"+userImg+"'")
                                                .replace("$currentTime",nick);
                                            answer = answer.replace("$answerImg",userImg);
                                            if(content.indexOf('sqi_upload_files')!=-1 && content.indexOf('ossdownload') == -1){
                                                if(content.indexOf('robotUploadFile')!=-1){//上传文件
                                                    content = getFileLink(content);
                                                }
                                                else if(content.indexOf("<img")==-1){
                                                    content ="<img  src='//"+hostName+"/robot/ossdownload.htm?filePath="+content+"'>";
                                                }
                                            }
                                            answer = answer.replace('$chatText',content);
                                            $('.robot-modal .chat-ul').append(answer);
                                            addTarget();
                                        }
                                        setHeight(100);
                                        showImg();
                                    }
                                }
                                else if(res.msgType == 'subscribed'){
                                    getUserStatus(is_manual ? 1 : 0);
                                    getHistoryDateList() ;
                                }
                            },function(msg,reSubscribeCb){//断线重连获取token
                                console.log(msg);
                                console.log(reSubscribeCb);
                                $.ajax({
                                    url:"//sqi.alibaba-inc.com/robot/chatBox/getApushToken.json",
                                    dataType:"jsonp",
                                    data:{
                                        productType:robotProductType,
                                        chatBu:chatBu,
                                        chatUser:robotUserId
                                    },
                                    jsonp:"callback", success:function(token){
                                        reSubscribeCb.call(null,token);
                                    }
                                });
                            });
                        }
                    });
                }
            });
        });
    }

    //修改人工/机器人状态，及结束服务，催一下按钮的展现
    function changeManualStatus(manualStatus){
        if(manualStatus){
            is_manual=true;
            $('.over-link').css('display','block');
            $('.sendDingding').css('display','block');
        }
        else{
            is_manual=false;
            manual_info=false;
            $('.over-link').css('display','none');
            $('.sendDingding').css('display','none');
        }

    }

    /* *****************************/
    /* 获取聊天记录的时间，内容，初始化  */
    /* *****************************/
    function getHistoryDateList(){
        $.ajax({
            url:"//"+hostName+"/robot/chatBox/getHistoryDateList.json",
            dataType:"jsonp",
            data:{
                productType:robotProductType,
                chatBu:chatBu,
                chatUser:robotUserId
            },
            jsonp:"callback",
            success:function(d){
                $('#sqiChatDateUl').html("");
                var content = "";
                var queryDate = "";
                for(var i=0;i<d.length;i++){
                    if(i==0)
                    {
                        content += "<li class='active J_chat_history_li'>"+ d[i]+"</li>";
                        queryDate = d[i];
                    }
                    else{
                        content += "<li class='J_chat_history_li'>"+ d[i]+"</li>";
                    }
                }
                $('#sqiChatDateUl').html(content);
                bindHistoryDateclick();
                getSqiChatHistory(robotProductType,chatBu,"#sqiChatHistoryUl",2000,queryDate,"","");
            }
        });
    }

    //获取历史聊天记录
    function getSqiChatHistory(productType,chatBu,selector,pageSize,queryDate,chatContent,sessionId){
        $.ajax({
            url:"//"+hostName+"/robot/chatBox/getHistoryChat.json",
            dataType:"jsonp",
            data:{
                productType:productType,
                chatBu:chatBu,
                chatOriginUser:robotUserId,
                chatUserCode :robotUserCode,
                pageSize:pageSize,
                queryDate:queryDate,
                chatContent:chatContent,
                sessionId:sessionId
            },
            jsonp:"callback",
            success:function(chatRecordList){
                $(selector).html("");
                if(chatRecordList.length >0 && $(selector).hasClass("chat-ul")){
                    var defaultAnswer="<span >"+lexicon["robotHello"]+robotNick+lexicon["Can I help you"]+"</span>";
                    var currentTime = "";
                    var answer = $(answerTemplate).html().replace("<img","<span class='triangle'></span><img").replace('$chatText',defaultAnswer).replace("$userNick",currentTime).replace('data="$answerImg"',"src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/robot.png'").replace('$currentTime',robotNick);
                    $(selector).append(answer);
                }
                for(var x=chatRecordList.length-1;x>=0;x--){
                    var nick=robotNick;
                    var content =chatRecordList[x].chatContent;
                    content = unescape(content);
                    content = unescapeHTML(content);
                    // content = content.replace(/&ampcurren/g,"&curren").replace(/&ampnot/g,"&not");
                    var currentTime = chatRecordList[x].chatTime;
                    var chatHtml="";
                    var userCode = 0;
                    var userImg = "//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/robot.png";
                    var systemFlag = chatRecordList[x].systemFlag;//是否系统消息
                    if(systemFlag==1){
                        var chatHtml ="<li class='center-info'><div class='li-message'>"+content+"</div></li>";
                    }
                    else{
                        if(chatRecordList[x].chatUser!=0) //非机器人
                        {
                            nick=chatRecordList[x].chatUserObject.nickOrRealName;
                            userCode = chatRecordList[x].chatUserObject.code;
                            userImg = "https://work.alibaba-inc.com/photo/"+chatRecordList[x].chatUserObject.code+".50x50.jpg";
                        }
                        //问题
                        if(chatRecordList[x].isChatQuestion==1){
                            content = content.httpHtml();
                            chatHtml = $(questionTemplate).html();
                        }
                        else{//答案
                            chatHtml = $(answerTemplate).html();
                        }
                        if(content.indexOf('sqi_upload_files')!=-1 && content.indexOf('ossdownload') == -1){
                            if(content.indexOf('robotUploadFile')!=-1){//上传文件
                                content = getFileLink(content);
                            }else if(content.indexOf("<img")==-1){//上传图片
                                content = "<img src='http://"+hostName+"/robot/ossdownload.htm?filePath="+content+"'>";
                            }
                        }
                        if(chatRecordList[x].chatUser == 0 && converter !="" && typeof (converter) != "undefiend"){
                            content = converter.makeHtml(content);
                        }
                        chatHtml = chatHtml.replace("<img","<span class='triangle'></span><img").replace('$chatText',content).replace("$userNick", currentTime).replace('data="$answerImg"',"src='"+userImg+"'").replace("$currentTime",nick).replace('data="https://work.alibaba-inc.com/photo/$userCode.50x50.jpg"',"src='https://work.alibaba-inc.com/photo/"+userCode+".50x50.jpg'");
                    }
                    $(selector).append(chatHtml);

                }
                setHeight(1000);
                showImg();
            }
        });
    }

    //聊天记录日期切换，聊天内容改变
    function bindHistoryDateclick(){
        $('.J_chat_history_li').click(function(e){
            $('.J_chat_history_li').removeClass("active");
            var chatContent = $('#searchSqiChatHistory').val();//要搜索的文字内容,这块功能还没有
            $(e.target).addClass("active");
            var date=$(e.target).html();
            getSqiChatHistory(robotProductType,chatBu,"#sqiChatHistoryUl",2000,date,chatContent,"");
        });
    }

    /* *****************************/
    /* 发送用户的消息到服务端，文字或图片  */
    /* *****************************/
    //flag=0 文字，flag=1 图片，flag=4，上传文件
    function setChatResponse(query,flag){
        if(query!=""){
            var userCode = robotUserCode;
            var userNick = robotUserNick;
            var currentTime = getNowFormatDate();
            var question = "";
            // 特殊处理
            if(query.indexOf("http")!=-1)
            {
                query = query.replace(/&curren/g,"&ampcurren").replace(/&not/g,"&ampnot");
                //避免url中包含一些特殊字符，如&curren,&not
            }
            var queryString = query;
            // 转换标签
            if(query.indexOf("<i class='ww-smiley'")!=0){
                query=query.replace(/</g, '<\001').replace(/>/g, '\001>');
            }
            //转换html标签
            if(checkHtml(query)){
                query = unescapeHTML(query);
            }
            if(query.indexOf("href")==-1 && checkHttp(query)){
                //转换里面的链接
                query = query.httpHtml();
            }
            question = $(questionTemplate).html().replace("<img","<span class='triangle'></span><img").replace('data="https://work.alibaba-inc.com/photo/$userCode.50x50.jpg"',"src='https://work.alibaba-inc.com/photo/"+userCode+".50x50.jpg'").replace('$userNick',currentTime ).replace('$currentTime',userNick);

            if(flag==0){
                question =question.replace('$chatText',query);
                $('.chat-ul').append(question);
                setHeight(100);
                showImg();
            }
            else if(flag==1 && customer){ //图片  // 关闭客服不支持文件上传和图片上传功能
                var timestamp = Date.parse(new Date());
                var imgDiv ="<img id='"+timestamp+"' src='//"+hostName+"/robot/ossdownload.htm?filePath="+query+"'>";
                question = question.replace('$chatText',imgDiv);
                changeManualStatus(true);
                $('.chat-ul').append(question);
                $('#'+timestamp).load(function(){
                    setHeight(100);
                    showImg();
                });
            }
            else if(flag==4 && customer){//文件  // 关闭客服不支持文件上传和图片上传功能
                var fileLink = getFileLink(query);
                question = question.replace('$chatText',fileLink);
                changeManualStatus(true);
                $('.chat-ul').append(question);
            }
            $('.robot-modal .self .chat-text>a').click(function(e){
                e.preventDefault();
                var link = $(e.target).attr('href');
                if(typeof(link)!='undefined' && link.indexOf("&amp")!=-1){
                    link = link.replace(/&amp/g,"&");
                }
                window.open(link);
            })
            //query = query.replace(/&/g,"%26");
            //机器人回复，展现正在输入的提醒
            if(is_manual==false){
                var defaultAnswer="<span id='robotInputTips'>"+lexicon["Typing"]+"<span class='robot-dotting'></span></span>";
                var currentTime = "";
                var answer = $(answerTemplate).html().replace("<img","<span class='triangle'></span><img")
                    .replace('$chatText',defaultAnswer)
                    .replace("$userNick",currentTime)
                    .replace('data="$answerImg"',"src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/robot.png'")
                    .replace('$currentTime',robotNick);
                $('.chat-panel .chat-ul').append(answer);
                addTarget();
            }

            setTimeout("$('#chatInput').val('')",10);
            $.ajax({
                url:"//"+hostName+"/robot/api/chatBoxGetResponse.json",
                data:{
                    is_manual:is_manual,
                    product_type:robotProductType,
                    chatBu:chatBu,
                    manual_info:manual_info,
                    chatUser:robotUserId,
                    flag:flag,
                    query:queryString,
                    customer:customer
                },
                type : "POST",
                success:function(d){
                    if(is_manual==true && manual_info==false && customer){//人工答疑
                        var userList = d.responserList ;
                        var userContent = "";
                        for(var i=0;i<userList.length;i++){
                            userContent = userContent + "<span data-widget='popuser' class='popuser'  data-id='" + userList[i].id + "'>" + userList[i].nickOrRealName + "</span> ";
                        }
                        manual_info =true;
                        var newHtml ="<li class='center-info'><div class='li-message'>"+lexicon["Customer Service"]+"："+userContent+lexicon["serve you"]+"</div></li>";
                        $('.chat-ul').append(newHtml);
                        setHeight(500);
                        showImg();
                    }
                    else{//机器自动答疑
                        //删除正在输入的提示内容
                        if(typeof($('#robotInputTips').parents('.chat-item'))!='undefined'){
                            $('#robotInputTips').parents('.chat-item').remove();
                        }
                        if(d.need_manual==true)
                        {
                            is_manual=true;
                        }
                        if(typeof(d.value)!='undefined' && typeof(d.value.result)!='undefined'){
                            var defaultAnswer=d.value.result.fulfillment.speech;
                            var type = d.value.result.metadata.intentMessageType;
                            var currentTime = d.chatRecord.chatTime;
                            if(type === "others"){
                                try {
                                  var message = JSON.parse(RFHTMLParserFunction.parseEnter(defaultAnswer));
                                  type = message.msgType;
                                  defaultAnswer = RFHTMLParserFunction.parseHtml(type,message);
                                }catch(e){
                                  console.log(e);
                                }
                            }else if(converter != "" && typeof (converter) != "undefiend"){
                                defaultAnswer = converter.makeHtml(defaultAnswer);
                            }
                            if(!checkHttp(defaultAnswer)){
                                //转换自动回复中的链接为可点击的a标签
                                defaultAnswer = defaultAnswer.httpHtml();
                            }
                            var answer = $(answerTemplate).html().replace("<img","<span class='triangle'></span><img")
                                .replace('$chatText',defaultAnswer)
                                .replace("$userNick",currentTime)
                                .replace('data="$answerImg"',"src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/robot.png'")
                                .replace('$currentTime',robotNick);
                            $('.chat-ul').append(answer);
                            addTarget();
                            if(d.need_manual!=true && customer){
                                var newHtml ="<li class='center-info'><div class='li-message '>"+lexicon["Don't solve your problems"]+"<a class='J_change_manual' style='cursor:pointer;'>"+lexicon["transfer to manual service"]+"</a></div></li>";
                                $('.chat-ul').append(newHtml);
                            }
                            setReadStatus(); //全部已读
                            if(typeof $('.chat-ul .other').find('.J_hot') != "undefined" && $('.chat-ul .other').find('.J_hot').length >0){
                                bindHotClick();  // 绑定J_hot事件
                            }
                            //转人工客服
                            $('.J_change_manual').click(function(e){
                                if(!is_manual)
                                {
                                    changeManualStatus(true);
                                    setChatResponse(query,3);
                                }
                            });
                        }
                        setHeight(500);
                        showImg();
                    }
                }
            });
        }
    }


    function getNowFormatDate() {
        var date = new Date();
        var seperator1 = "-";
        var seperator2 = ":";
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        var strHour = date.getHours();
        var strMinutes=  date.getMinutes();
        var strSeconds= date.getSeconds();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        if (strHour >= 0 && strHour <= 9) {
            strHour = "0" + strHour;
        }
        if (strMinutes >= 0 && strMinutes <= 9) {
            strMinutes = "0" + strMinutes;
        }
        if (strSeconds >= 0 && strSeconds <= 9) {
            strSeconds = "0" + strSeconds;
        }
        var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + strHour + seperator2 + strMinutes + seperator2 + strSeconds;
        return currentdate;
    }



    //批量更新已读状态
    function setReadStatus(){
        if($('.robot-parent').css('display') =='block'){
            $.ajax({
                url:"//"+hostName+"/robot/chatBox/setChatReadStatus.json",
                data:{
                    chatBu:chatBu,
                    productType:robotProductType,
                    chatUser:robotUserId
                },
                dataType:"jsonp",
                jsonp:"callback",
                success:function(d){
                }
            });
        }
    }

    //人工状态下加个轮训，一分钟一次，避免apush挂了，没有重新连上，
    setInterval(function(){
        if(is_manual==true){
            getUserStatus(1);
        }
    },60000);

    //获取当前用户最后一个会话状态，更新全局变量，是否人工模式，已读未读，如果已读且已经关闭，则不显示历史记录，否则显示上一次会话的聊天记录
    function getUserStatus(intervalFlag){
        $.ajax({
            url:"//"+hostName+"/robot/api/getUserLastSessionStatus.json",
            dataType:"jsonp",
            data:{
                productType:robotProductType,
                chatBu:chatBu,
                userId:robotUserId,
                userCode:robotUserCode
            },
            jsonp:"callback",
            success:function(d){
                if(d.status=='close')//机器人状态
                {
                    changeManualStatus(false);
                }
                else{//人工状态
                    changeManualStatus(true);
                }
                if(intervalFlag==1){//全局变量是人工
                    var sessionId = d.sessionId;
                    getSqiChatHistory(robotProductType,chatBu,".chat-panel .chat-ul",2000,"","",sessionId);//获取最后一个session的记录
                }
                if(intervalFlag==0){
                    if(d.status=='open' || d.noRead>0)
                    {
                        var sessionId = d.sessionId;
                        getSqiChatHistory(robotProductType,chatBu,".chat-panel .chat-ul",2000,"","",sessionId);//获取最后一个session的记录
                    }
                    else{
                        var defaultAnswer="<span >亲，我是答疑"+robotNick+"，需要我为您做点什么~~</span>";
                        var currentTime = "";
                        var answer = $(answerTemplate).html().replace("<img","<span class='triangle'></span><img").replace('$chatText',defaultAnswer).replace("$userNick",currentTime).replace('data="$answerImg"',"src='//sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/robot.png'").replace('$currentTime',robotNick);
                        $('.chat-ul').append(answer);
                        addTarget();
                    }
                }
            }
        })
    }


    /* ******************** */
    /* 表情内容生成及事件绑定  */
    /* ******************** */
    function setEmoji(){
        var emojiContent = '<li class="ww-smiley" data-id="[微笑]" style="background-position:0 0"></li><li class="ww-smiley" data-id="[害羞]" style="background-position:0 -25px"></li><li class="ww-smiley" data-id="[吐舌头]" style="background-position:0 -50px"></li><li class="ww-smiley" data-id="[偷笑]" style="background-position:0 -75px"></li><li class="ww-smiley" data-id="[爱慕]" style="background-position:0 -100px"></li><li class="ww-smiley" data-id="[大笑]" style="background-position:0 -125px"></li><li class="ww-smiley" data-id="[跳舞]" style="background-position:0 -150px"></li><li class="ww-smiley" data-id="[飞吻]" style="background-position:0 -175px"></li><li class="ww-smiley" data-id="[安慰]" style="background-position:0 -200px"></li><li class="ww-smiley" data-id="[抱抱]" style="background-position:0 -225px"></li><li class="ww-smiley" data-id="[加油]" style="background-position:0 -250px"></li><li class="ww-smiley" data-id="[胜利]" style="background-position:0 -275px"></li><li class="ww-smiley" data-id="[强]" style="background-position:0 -300px"></li><li class="ww-smiley" data-id="[亲亲]" style="background-position:0 -325px"></li><li class="ww-smiley" data-id="[花痴]" style="background-position:0 -350px"></li><li class="ww-smiley" data-id="[露齿笑]" style="background-position:0 -375px"></li><li class="ww-smiley" data-id="[查找]" style="background-position:0 -400px"></li><li class="ww-smiley" data-id="[呼叫]" style="background-position:0 -425px"></li><li class="ww-smiley" data-id="[算账]" style="background-position:0 -450px"></li><li class="ww-smiley" data-id="[财迷]" style="background-position:0 -475px"></li><li class="ww-smiley" data-id="[好主意]" style="background-position:0 -500px"></li><li class="ww-smiley" data-id="[鬼脸]" style="background-position:0 -525px"></li><li class="ww-smiley" data-id="[天使]" style="background-position:0 -550px"></li><li class="ww-smiley" data-id="[再见]" style="background-position:0 -575px"></li><li class="ww-smiley" data-id="[流口水]" style="background-position:0 -600px"></li><li class="ww-smiley" data-id="[享受]" style="background-position:0 -625px"></li><li class="ww-smiley" data-id="[色情狂]" style="background-position:0 -650px"></li><li class="ww-smiley" data-id="[呆若木鸡]" style="background-position:0 -675px"></li><li class="ww-smiley" data-id="[思考]" style="background-position:0 -700px"></li><li class="ww-smiley" data-id="[迷惑]" style="background-position:0 -725px"></li><li class="ww-smiley" data-id="[疑问]" style="background-position:0 -750px"></li><li class="ww-smiley" data-id="[没钱了]" style="background-position:0 -775px"></li><li class="ww-smiley" data-id="[无聊]" style="background-position:0 -800px"></li><li class="ww-smiley" data-id="[怀疑]" style="background-position:0 -825px"></li><li class="ww-smiley" data-id="[嘘]" style="background-position:0 -850px"></li><li class="ww-smiley" data-id="[小样]" style="background-position:0 -875px"></li><li class="ww-smiley" data-id="[摇头]" style="background-position:0 -900px"></li><li class="ww-smiley" data-id="[感冒]" style="background-position:0 -925px"></li><li class="ww-smiley" data-id="[尴尬]" style="background-position:0 -950px"></li><li class="ww-smiley" data-id="[傻笑]" style="background-position:0 -975px"></li><li class="ww-smiley" data-id="[不会吧]" style="background-position:0 -1000px"></li><li class="ww-smiley" data-id="[无奈]" style="background-position:0 -1025px"></li><li class="ww-smiley" data-id="[流汗]" style="background-position:0 -1050px"></li><li class="ww-smiley" data-id="[凄凉]" style="background-position:0 -1075px"></li><li class="ww-smiley" data-id="[困了]" style="background-position:0 -1100px"></li><li class="ww-smiley" data-id="[晕]" style="background-position:0 -1125px"></li><li class="ww-smiley" data-id="[忧伤]" style="background-position:0 -1150px"></li><li class="ww-smiley" data-id="[委屈]" style="background-position:0 -1175px"></li><li class="ww-smiley" data-id="[悲泣]" style="background-position:0 -1200px"></li><li class="ww-smiley" data-id="[大哭]" style="background-position:0 -1225px"></li><li class="ww-smiley" data-id="[痛哭]" style="background-position:0 -1250px"></li><li class="ww-smiley" data-id="[I服了U]" style="background-position:0 -1275px"></li><li class="ww-smiley" data-id="[对不起]" style="background-position:0 -1300px"></li><li class="ww-smiley" data-id="[再见]" style="background-position:0 -1325px"></li><li class="ww-smiley" data-id="[皱眉]" style="background-position:0 -1350px"></li><li class="ww-smiley" data-id="[好累]" style="background-position:0 -1375px"></li><li class="ww-smiley" data-id="[吐]" style="background-position:0 -1400px"></li><li class="ww-smiley" data-id="[背]" style="background-position:0 -1425px"></li><li class="ww-smiley" data-id="[惊讶]" style="background-position:0 -1450px"></li><li class="ww-smiley" data-id="[惊愕]" style="background-position:0 -1475px"></li><li class="ww-smiley" data-id="[闭嘴]" style="background-position:0 -1500px"></li><li class="ww-smiley" data-id="[欠扁]" style="background-position:0 -1525px"></li><li class="ww-smiley" data-id="[鄙视你]" style="background-position:0 -1550px"></li><li class="ww-smiley" data-id="[大怒]" style="background-position:0 -1575px"></li><li class="ww-smiley" data-id="[生气]" style="background-position:0 -1600px"></li><li class="ww-smiley" data-id="[财神]" style="background-position:0 -1625px"></li><li class="ww-smiley" data-id="[学习雷锋]" style="background-position:0 -1650px"></li><li class="ww-smiley" data-id="[恭喜发财]" style="background-position:0 -1675px"></li><li class="ww-smiley" data-id="[小二]" style="background-position:0 -1700px"></li><li class="ww-smiley" data-id="[老大]" style="background-position:0 -1725px"></li><li class="ww-smiley" data-id="[邪恶]" style="background-position:0 -1750px"></li><li class="ww-smiley" data-id="[单挑]" style="background-position:0 -1775px"></li><li class="ww-smiley" data-id="[CS]" style="background-position:0 -1800px"></li><li class="ww-smiley" data-id="[隐形人]" style="background-position:0 -1825px"></li><li class="ww-smiley" data-id="[炸弹]" style="background-position:0 -1850px"></li><li class="ww-smiley" data-id="[惊声尖叫]" style="background-position:0 -1875px"></li><li class="ww-smiley" data-id="[漂亮MM]" style="background-position:0 -1900px"></li><li class="ww-smiley" data-id="[帅哥]" style="background-position:0 -1925px"></li><li class="ww-smiley" data-id="[招财猫]" style="background-position:0 -1950px"></li><li class="ww-smiley" data-id="[成交]" style="background-position:0 -1975px"></li><li class="ww-smiley" data-id="[鼓掌]" style="background-position:0 -2000px"></li><li class="ww-smiley" data-id="[握手]" style="background-position:0 -2025px"></li><li class="ww-smiley" data-id="[红唇]" style="background-position:0 -2050px"></li><li class="ww-smiley" data-id="[玫瑰]" style="background-position:0 -2075px"></li><li class="ww-smiley" data-id="[残花]" style="background-position:0 -2100px"></li><li class="ww-smiley" data-id="[爱心]" style="background-position:0 -2125px"></li><li class="ww-smiley" data-id="[心碎]" style="background-position:0 -2150px"></li><li class="ww-smiley" data-id="[钱]" style="background-position:0 -2175px"></li><li class="ww-smiley" data-id="[购物]" style="background-position:0 -2200px"></li><li class="ww-smiley" data-id="[礼物]" style="background-position:0 -2225px"></li><li class="ww-smiley" data-id="[收邮件]" style="background-position:0 -2250px"></li><li class="ww-smiley" data-id="[电话]" style="background-position:0 -2275px"></li><li class="ww-smiley" data-id="[举杯庆祝]" style="background-position:0 -2300px"></li><li class="ww-smiley" data-id="[时钟]" style="background-position:0 -2325px"></li><li class="ww-smiley" data-id="[等待]" style="background-position:0 -2350px"></li><li class="ww-smiley" data-id="[很晚了]" style="background-position:0 -2375px"></li><li class="ww-smiley" data-id="[飞机]" style="background-position:0 -2400px"></li><li class="ww-smiley" data-id="[支付宝]" style="background-position:0 -2425px"></li>';
        $('#emojiSqi').html(emojiContent);
        $('.ww-smiley').click(function(e){
            var style = $(e.target).attr('style');
            var dataId=$(e.target).attr('data-id');
            var newQuery = "<i class='ww-smiley' style='"+style+"' data-id='"+dataId+"'></i>";
            setChatResponse(newQuery,0);
            $('#chatEmojiPanel').css('display','none');
            $('#chatEmojiPanel').removeClass("active");
        });
        $('#chatBoxEmoji').click(function(e){
            var className=$('#chatEmojiPanel').attr("class");
            if(className.indexOf("active")==-1)
            {
                $('#chatEmojiPanel').addClass("active");
                $('#chatEmojiPanel').css('display','block');
            }
            else
            {
                $('#chatEmojiPanel').removeClass("active");
                $('#chatEmojiPanel').css('display','none');
            }
        });
        //点击页面其他位置，表情弹出窗口关闭
        $(document).click(function(e){
            if($(e.target).parents('#emojiPanel').length==0 && $(e.target).parents('#chatBoxEmoji').length==0 && $(e.target).attr('id')!='chatBoxEmoji' && $(e.target).attr('id')!='emojiPanel'){
                $('#chatEmojiPanel').css('display','none');
                $('#chatEmojiPanel').removeClass("active");
            }
        });
    }

    //支持文件上传功能，根据文件上传后的url，生成页面元素
    function getFileLink(query){
        var fileName = query.split("filePath=")[1].split("fileName=")[1];
        var fileDownLoadIcon ="<img style='width: 13px;' src='http://sqi.alicdn.com/sqi-pub/pro/assets/si/robot/css/img/fileDownload.png'>";
        var fileLink = "<a href='http://"+hostName+"/robot/"+query+"'>"+fileDownLoadIcon+fileName+"</a>";
        return fileLink;
    }

})

// 连接都打开新页面
function addTarget() {
    $(".chat-ul a[href^='http']").attr("target","_blank");
}
