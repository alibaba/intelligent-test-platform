'use strict';

/* Filters */
// need load the moment.js to use this filter. 
angular.module('app.filters', [])
  .filter('fromNow', function() {
    return function(date) {
      return moment(date).fromNow();
    }
  })
.filter('secondFormat',function(){
    return function(total){
        if(!total)return "-";
        if(total<=0)return "-";
        if(total<60)return total+"s";
        var min = 60;
        var hour = 60*min;
        var day = 24*hour;
        var remaining_day = Math.floor(total/day);
        var remaining_hour = Math.floor((total-day*remaining_day)/hour);
        var remain_min = Math.floor((total-day*remaining_day - hour*remaining_hour)/min);
        var remain_sec = total-day*remaining_day - hour*remaining_hour - min*remain_min;
        var remainInfo = (remaining_day == 0?"":(remaining_day+"d"))+
            (remaining_hour == 0?"":(remaining_hour+"h"))+
            (remain_min == 0?"":(remain_min+"m"))+
            (remain_sec == 0?"":(remain_sec+"s"));
        return remainInfo;
    }
})
  .filter('propsFilter', function() {
    return function(items, props) {
        var out = [];

        if (angular.isArray(items)) {
            items.forEach(function(item) {
                var itemMatches = false;

                var keys = Object.keys(props);
                for (var i = 0; i < keys.length; i++) {
                    var prop = keys[i];
                    var text = props[prop].toLowerCase();
                    if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
                        itemMatches = true;
                        break;
                    }
                }

                if (itemMatches) {
                    out.push(item);
                }
            });
        } else {
            // Let the output be the input untouched
            out = items;
        }

        return out;
    };
})
    .filter('longWordFormat',function(){
    return function(ext,len){
        var reg = new RegExp("[\\u4E00-\\u9FFF]+","g");
        var uppercase = false;

        if(ext && (typeof ext == "string")){
            if(!reg.test(ext)){
                for(var i=0;i<ext.length;i++){
                    var c=ext.charAt(i);
                    if(/[A-Z]/.test(c)){
                        uppercase = true;
                        break;
                    }
                }
                if(!uppercase)len = len * 2;
            }
            if(ext.length<=len)return ext;
            return ext.substring(0,len)+"...";
        } else return "";
    }
})
    .filter('formatBr',function(){
        return function(ext){
            if(ext && (typeof ext == "string")){
                return ext.replace("\n","<br>").replace("\r","<br>");
            } else return "";
        }
    })
    .filter('hostTypeName',function(){
        return function(t){
            if(!t || t=="null" || t == 0 || t=="offline"){
                return "线下";
            }
            if(t == 1 || t=="online"){
                return "线上";
            } else if(t == 2 || t=="preline"){
                return "预发";
            } else {
                return t;
            }
        }
    });