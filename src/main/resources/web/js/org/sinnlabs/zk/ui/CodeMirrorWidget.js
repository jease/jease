/**
 * CodeMirrorWidget.js
 * Purpose:
 * 
 * Description:
 * 
 * History:
		Tue Nov 15 14:47:28     2015, Created by peter.liverovsky
 * Copyright (C) 2015 Sinnlabs LTD. All Rights Reserved.
 * This program is distributed under LGPL Version 2.1 in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY.
*/

zk.$package('org.sinnlabs.zk.ui');

(function () {

	function stopOnChanging_(wgt) {
		if (wgt._tChg) {
			clearTimeout(wgt._tChg);
			wgt._tChg = null;
		}
	}
	
	function startOnChanging_(wgt) {
		stopOnChanging_(wgt);
		wgt._tChg = setTimeout(wgt.proxy(onChanging_), zul.inp.InputWidget.onChangingDelay);
	}
	
	function onChanging_() {
		this.fireOnChange();
	}

	var SourceWidget =
		/**
		 * <p>The delay to send the onChanging event is controlled by
		 * {@link #onChangingDelay}, which is default to 350.
		 * To change it, you can specify the following in a ZUL file.
		 * <pre><code>
		   &lt;?script content="zk.afterLoad('zul.inp',function(){zul.inp.InputWidget.onChangingDelay=1000;})"?&gt;
		   </code></pre>
		 */
		org.sinnlabs.zk.ui.CodeMirrorWidget = zk.$extends(zul.inp.Textbox, {

			_codemirror: null,
			
			_mode: "text/html",
			
			// For some mysterious reason initial value must not be true, 
			// otherwise showing line numbers won't be runtime on/off switchable. 
			_lineNumbers: false, 

			$init: function () { 
				this.$supers('$init', arguments);
				//zWatch.listen({onSize: [this, this.onSize_]});
			},

			bind_: function (dt, skipper, after) {
				this.$supers('bind_', arguments);
				var wgt = this;
				this._codemirror = CodeMirror.fromTextArea(this.$n('codemirror'), {
					lineNumbers: wgt._lineNumbers,
					mode: wgt._mode,
					matchBrackets: true
				});
				this._codemirror.on('blur', function () {
					var val = wgt._codemirror.getValue();
					wgt.setValue(val);
					if (wgt._tChg) {
						clearTimeout(wgt._tChg);
						wgt._tChg = null;
					}
					wgt.fireOnChange();
				});
				this._codemirror.on('keydown', function (evnt) {
					stopOnChanging_(wgt); //wait for key up
				});
				this._codemirror.on('keyup', function (evnt) {
					startOnChanging_(wgt);
				});
				this._multiline = true;
				zWatch.listen({onSize: this});
				this.refresh();
			},
			
			unbind_: function () {
				this.$supers('unbind_', arguments);
				zWatch.unlisten({onSize: this});
			},

			fireOnChange: function () {
				var val = this._codemirror.getValue();
				this.setValue(val);
				this.fire('onChange', {value: val}, {toServer: true});
			},
			
			onSize: function () {
				this.refresh();
			},
			
			setMode: function (val) {
				if (this._mode != val) {
					this._mode = val;
					if (this._codemirror) {
						this._codemirror.setOption("mode", this._mode);
					}
				}
			},
			
			getMode: function() {
				return this._mode;
			},
			
			setValue: function(txt, fromserver) {
				this.$supers('setValue', arguments);
				if (this._codemirror) {
					//if (fromserver) // Fix chrome browser bug
					//	this._codemirror.setValue(this.getValue()); // breaks Chrome 71 !!!
				}
			},
			
			setLineNumbers: function (val) {
                if (this._lineNumbers != val) {
                    this._lineNumbers = val;
                    if (this._codemirror) {
                        this._codemirror.setOption("lineNumbers", this._lineNumbers);
                    }
                }
            },
            
            getLineNumbers: function() {
                return this._lineNumbers;
            },
			
			refresh: function() {
				$('.CodeMirror').each(function(i, el) {
				    el.CodeMirror.refresh();
				});
			}
		});
})();