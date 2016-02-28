CKEDITOR.editorConfig = function(config) {
	config.entities = false;
	config.entities_latin = false;
	config.pasteFromWordRemoveStyles = true;
	config.pasteFromWordRemoveFontStyles =  true;
	config.toolbar = [
			[ 'NewPage', 'Preview', '-' ],
			[ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Print'],
			[ 'Undo', 'Redo', '-', 'Find', 'Replace', '-', 'SelectAll', 'RemoveFormat', 'Maximize', 'ShowBlocks', '-', 'About' ],
			'/',
			[ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript' ],
			[ 'NumberedList', 'BulletedList', 'Outdent', 'Indent', 'Blockquote', 'CreateDiv' ],
			[ 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ],
			[ 'SpellChecker', 'Scayt' ],
			'/',
			[ 'Format', 'TextColor', 'BGColor' ],
			[ 'Link', 'Unlink', 'Anchor' ],
			[ 'Image', 'Flash', 'Table', 'HorizontalRule', 'SpecialChar', 'PageBreak', '-', 'Source' ] 
	];
};

// Fix paste from Word/OpenOffice etc.
// See http://stackoverflow.com/questions/5227140
CKEDITOR.on('instanceReady', function(ev) {
	ev.editor.on('paste', function(evt) {    
		evt.data['html'] = '<!--class="Mso"-->'+evt.data['html'];
	}, null, null, 9);
});
