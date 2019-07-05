var SA = {};

SA.currentSelection = {
    "startContainer": 0,
    "startOffset": 0,
    "endContainer": 0,
    "endOffset": 0};

SA.editor = document.getElementById('editor');

document.addEventListener("selectionchange", function() { SA.backuprange(); });

// Initializations
SA.callback = function() {
    window.location.href = "sa-callback://" + encodeURI(SA.getHtml());
}

SA.setHtml = function(contents) {
    SA.editor.innerHTML = decodeURIComponent(contents.replace(/\+/g, '%20'));
}

SA.getHtml = function() {
    return SA.editor.innerHTML;
}

SA.getText = function() {
    return SA.editor.innerText;
}

SA.setBaseTextColor = function(color) {
    SA.editor.style.color  = color;
}

SA.setBaseFontSize = function(size) {
    SA.editor.style.fontSize = size;
}

SA.setPadding = function(left, top, right, bottom) {
  SA.editor.style.paddingLeft = left;
  SA.editor.style.paddingTop = top;
  SA.editor.style.paddingRight = right;
  SA.editor.style.paddingBottom = bottom;
}

SA.setBackgroundColor = function(color) {
    document.body.style.backgroundColor = color;
}

SA.setBackgroundImage = function(image) {
    SA.editor.style.backgroundImage = image;
}

SA.setWidth = function(size) {
    SA.editor.style.minWidth = size;
}

SA.setHeight = function(size) {
    SA.editor.style.height = size;
}

SA.setTextAlign = function(align) {
    SA.editor.style.textAlign = align;
}

SA.setVerticalAlign = function(align) {
    SA.editor.style.verticalAlign = align;
}

SA.setPlaceholder = function(placeholder) {
    SA.editor.setAttribute("placeholder", placeholder);
}

SA.setInputEnabled = function(inputEnabled) {
    SA.editor.contentEditable = String(inputEnabled);
}

SA.undo = function() {
    document.execCommand('undo', false, null);
}

SA.redo = function() {
    document.execCommand('redo', false, null);
}


SA.setTextColor = function(color) {
    SA.restorerange();
    document.execCommand("styleWithCSS", null, true);
    document.execCommand('foreColor', false, color);
    document.execCommand("styleWithCSS", null, false);
}

SA.setTextBackgroundColor = function(color) {
    SA.restorerange();
    document.execCommand("styleWithCSS", null, true);
    document.execCommand('hiliteColor', false, color);
    document.execCommand("styleWithCSS", null, false);
}

SA.setFontSize = function(fontSize){
    document.execCommand("fontSize", false, fontSize);
}



SA.insertImage = function(url, alt) {
    var html = '<img id="myImg" src="'+ url +'" onClick="image()" alt="Snow" style="width:98%;max-width:100%;text-align: center;"><br/>';
    SA.insertHTML(html);
}


SA.insertVideos = function(url) {
    var html = '<video width="100%" controls><source src="'+url+'" type="video/mp4"><source src="'+url+'" type="video/ogg">Something was wrong with this file.</video> <br>';
    SA.insertHTML(html);
}

SA.insertHTML = function(html) {
    SA.restorerange();
    document.execCommand('insertHTML', false, html);
}

SA.insertLink = function(url, title) {
    SA.restorerange();
    var sel = document.getSelection();
    if (sel.toString().length == 0) {
        document.execCommand("insertHTML",false," <a href='"+url+"' target='blank'>"+title+"</a> ");
    } else if (sel.rangeCount) {
       var el = document.createElement("a");
       el.setAttribute("href", url);
       el.setAttribute("title", title);

       var range = sel.getRangeAt(0).cloneRange();
       range.surroundContents(el);
       sel.removeAllRanges();
       sel.addRange(range);
   }
    SA.callback();
}

SA.setTodo = function(text) {
    var html = '<br/> <input type="checkbox" name="'+ text +'" value="'+ text +'"/> &nbsp;';
    document.execCommand('insertHTML', false, html);
}


SA.prepareInsert = function() {
    SA.backuprange();
}

SA.backuprange = function(){
    var selection = window.getSelection();
    if (selection.rangeCount > 0) {
      var range = selection.getRangeAt(0);
      SA.currentSelection = {
          "startContainer": range.startContainer,
          "startOffset": range.startOffset,
          "endContainer": range.endContainer,
          "endOffset": range.endOffset};
    }
}

SA.restorerange = function(){
    var selection = window.getSelection();
    selection.removeAllRanges();
    var range = document.createRange();
    range.setStart(SA.currentSelection.startContainer, SA.currentSelection.startOffset);
    range.setEnd(SA.currentSelection.endContainer, SA.currentSelection.endOffset);
    selection.addRange(range);
}

SA.enabledEditingItems = function(e) {
    var items = [];
    if (document.queryCommandState('bold')) {
        items.push('bold');
    }
    if (document.queryCommandState('italic')) {
        items.push('italic');
    }
    if (document.queryCommandState('subscript')) {
        items.push('subscript');
    }
    if (document.queryCommandState('superscript')) {
        items.push('superscript');
    }
    if (document.queryCommandState('strikeThrough')) {
        items.push('strikeThrough');
    }
    if (document.queryCommandState('underline')) {
        items.push('underline');
    }
    if (document.queryCommandState('insertOrderedList')) {
        items.push('orderedList');
    }
    if (document.queryCommandState('insertUnorderedList')) {
        items.push('unorderedList');
    }
    if (document.queryCommandState('justifyCenter')) {
        items.push('justifyCenter');
    }
    if (document.queryCommandState('justifyFull')) {
        items.push('justifyFull');
    }
    if (document.queryCommandState('justifyLeft')) {
        items.push('justifyLeft');
    }
    if (document.queryCommandState('justifyRight')) {
        items.push('justifyRight');
    }
    if (document.queryCommandState('insertHorizontalRule')) {
        items.push('horizontalRule');
    }
    var formatBlock = document.queryCommandValue('formatBlock');
    if (formatBlock.length > 0) {
        items.push(formatBlock);
    }

    window.location.href = "sa-state://" + encodeURI(items.join(','));
}

SA.focus = function() {
    var range = document.createRange();
    range.selectNodeContents(SA.editor);
    range.collapse(true);
    var selection = window.getSelection();
    selection.removeAllRanges();
    selection.addRange(range);
    SA.editor.focus();
}

SA.blurFocus = function() {
    SA.editor.blur();
}

SA.removeFormat = function() {
    document.execCommand('removeFormat', false, null);
}

// Event Listeners
SA.editor.addEventListener("input", SA.callback);
SA.editor.addEventListener("keyup", function(e) {
    var KEY_LEFT = 37, KEY_RIGHT = 39;
    if (e.which == KEY_LEFT || e.which == KEY_RIGHT) {
        SA.enabledEditingItems(e);
    }
});

SA.setBold = function() {
    document.execCommand('bold', false, null);
}

SA.setItalic = function() {
    document.execCommand('italic', false, null);
}

SA.setSubscript = function() {
    document.execCommand('subscript', false, null);
}

SA.setSuperscript = function() {
    document.execCommand('superscript', false, null);
}

SA.setStrikeThrough = function() {
    document.execCommand('strikeThrough', false, null);
}

SA.setUnderline = function() {
    document.execCommand('underline', false, null);
}

SA.setBullets = function() {
    document.execCommand('insertUnorderedList', false, null);
}

SA.setNumbers = function() {
    document.execCommand('insertOrderedList', false, null);
}

SA.setHeading = function(heading) {
    document.execCommand('formatBlock', false, '<h'+heading+'>');
}

SA.setIndent = function() {
    document.execCommand('indent', false, null);
}

SA.setOutdent = function() {
    document.execCommand('outdent', false, null);
}

SA.setJustifyLeft = function() {
    document.execCommand('justifyLeft', false, null);
}

SA.setJustifyCenter = function() {
    document.execCommand('justifyCenter', false, null);
}

SA.setJustifyRight = function() {
    document.execCommand('justifyRight', false, null);
}

SA.setBlockquote = function() {
    document.execCommand('formatBlock', false, '<blockquote>');
}

SA.editor.addEventListener("click", SA.enabledEditingItems);
