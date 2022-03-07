调用如下：
-------------------------------------------
TyTts.getInstance().init(this);
TyTts.getInstance().play("你好TTS。");
-------------------------------------------

4.2 参数的设置
在ivTTS.h头文件中定义了每个参数的ID值，通过ID就可以对指定参数的值进行设置或读取。其中每个参数的意义如下：
/* constants for values of field nParamID */
#define ivTTS_PARAM_PARAMCH_CALLBACK	0x00000000	/* 参数改变回调函数参数 */
#define ivTTS_PARAM_LANGUAGE			0x00000100	/* 语种参数*/
#define ivTTS_PARAM_INPUT_CODEPAGE		0x00000101	/* 输入文本代码页参数 */
#define ivTTS_PARAM_TEXT_MARK			0x00000102	/* 文本标记参数 */
#define ivTTS_PARAM_USE_PROMPTS			0x00000104	/* 提示音参数，是否使用提示音。默认值为ivTrue，使用提示音。 */
#define ivTTS_PARAM_RECOGNIZE_PHONEME	0x00000105	/* 识别音标参数，是否识别音标（包括汉语拼音）输入。默认值为ivFalse，不识别。 */
#define ivTTS_PARAM_INPUT_MODE			0x00000200	/* i输入模式参数。默认为块式输入方式，流式输入不推荐使用。 */
#define ivTTS_PARAM_INPUT_TEXT_BUFFER	0x00000201	/* 合成文本指针，仅在块式输入模式（ivTTS_INPUT_FIXED_BUFFER）下有效。  */
#define ivTTS_PARAM_INPUT_TEXT_SIZE		0x00000202	/* 合成文本大小，仅在块式输入模式（ivTTS_INPUT_FIXED_BUFFER）下有效。 */
#define ivTTS_PARAM_INPUT_CALLBACK		0x00000203	/* 输入回调参数。在流式输入模式（ivTTS_INPUT_CALLBACK）下必须设置该参数值，流式输入方式是Aisound不推荐使用的，所以输入回调函数用户可以不用实现，该参数也不用设置了。  */
#define ivTTS_PARAM_PROGRESS_BEGIN		0x00000204	/* 在支持进度通知回调时，可获取进度的当前位置。 */
#define ivTTS_PARAM_PROGRESS_LENGTH		0x00000205	/* 在支持进度通知回调时，可获取进度的长度。 */
#define ivTTS_PARAM_PROGRESS_CALLBACK	0x00000206	/* 处理进度通知回调函数参数*/
#define ivTTS_PARAM_READ_AS_NAME		0x00000301	/* 姓名读音参数。是否强制姓氏发音方式。默认值为ivFalse，不强制姓氏朗读。 */
#define ivTTS_PARAM_READ_DIGIT			0x00000302	/* 数字读法参数 */
#define ivTTS_PARAM_CHINESE_NUMBER_1	0x00000303	/* 中文号码“1”的读法参数。  */
#define ivTTS_PARAM_MANUAL_PROSODY		0x00000304	/* 中文韵律标注。是否进行中文韵律标注，默认值为ivFalse，不识别韵律标注。  */
#define ivTTS_PARAM_ENGLISH_NUMBER_0	0x00000305	/* 英文号码“0”的读法参数。  */
#define ivTTS_PARAM_READ_WORD           0x00000306	/* 英文单词的朗读方式。  */
#define ivTTS_PARAM_OUTPUT_CALLBACK	    0x00000401	/* 输出回调参数。 */
#define ivTTS_PARAM_ROLE				0x00000500	/* 角色参数。 */
#define ivTTS_PARAM_SPEAK_STYLE			0x00000501	/* 发音风格参数。 */
#define ivTTS_PARAM_VOICE_SPEED			0x00000502	/* 语速参数。  */
#define ivTTS_PARAM_VOICE_PITCH			0x00000503	/* 语调参数。  */
#define ivTTS_PARAM_VOLUME				0x00000504	/* 音量参数。  */
#define ivTTS_PARAM_CHINESE_ROLE        0x00000510	/* 中文（包括混读）角色参数，只改变中文发音人。 */
#define ivTTS_PARAM_ENGLISH_ROLE        0x00000511	/* 英文角色参数，只改变英文发音人。  */
#define ivTTS_PARAM_VEMODE				0x00000600	/* 声音特效预置模式。 */
#define ivTTS_PARAM_USERMODE			0x00000701	/* 设置应用场景。默认根据编译的版本设置。  */
#define ivTTS_PARAM_NAVIGATION_MODE		0x00000701	/* 导航版本*/
#define ivTTS_PARAM_EVENT_CALLBACK		0x00001001	/* 事件回调函数参数。  */
#define ivTTS_PARAM_OUTPUT_BUF			0x00001002	/* 输出缓冲区参数。指定缓冲区地址和缓冲区大小后，合成数据输出为用户主动获取。该缓冲区由TTS实例管理，用户只需调用ivTTS_GetData来取数据。 */
#define ivTTS_PARAM_OUTPUT_BUFSIZE		0x00001003	/* 输出缓冲区大小参数。指定缓冲区地址和缓冲区大小后，合成数据输出为用户主动获取。该缓冲区由TTS实例管理，用户只需调用ivTTS_GetData来取数据。 */
#define ivTTS_PARAM_DELAYTIME			0x00001004	/* 初始缓冲时间参数。  */

下面针对一些常用的参数的值进行分析：
4.2.1 语种
/* constants for values of parameter ivTTS_PARAM_LANGUAGE */
#define ivTTS_LANGUAGE_AUTO             0           /* 自动判断。（默认） */
#define ivTTS_LANGUAGE_CHINESE			1			/* 汉语普通话。 (with English) */
#define ivTTS_LANGUAGE_ENGLISH			2			/* 英语。 */

4.2.2 文本标记
/* constants for values of parameter ivTTS_PARAM_TEXT_MARK */
#define ivTTS_TEXTMARK_NONE				0			/* 无标记。 */
#define ivTTS_TEXTMARK_SIMPLE_TAGS		1			/* 简单标记（默认值）。  */

4.2.3 数字读法
/* constants for values of parameter ivTTS_PARAM_READ_DIGIT */
#define ivTTS_READDIGIT_AUTO			0			/* 自动判断。默认值。  */
#define ivTTS_READDIGIT_AS_NUMBER	    1			/* 按号码方式读数字。  */
#define ivTTS_READDIGIT_AS_VALUE		2			/* 按数值方式读数字。  */

4.2.4 角色
/* constants for values of parameter ivTTS_PARAM_SPEAKER */
#define ivTTS_ROLE_TIANCHANG			1			/* 天畅（女声）。  */
#define ivTTS_ROLE_WENJING				2			/* 文静（女声）。  */
#define ivTTS_ROLE_XIAOYAN				3			/* 晓燕（女声）。  */
#define ivTTS_ROLE_XIAOFENG			    4			/* 小峰（男声）。  */
#define ivTTS_ROLE_SHERRI				5			/* Sherri（女声） */
#define ivTTS_ROLE_XIAOJIN				6			/* 晓晋（女声）。  */
#define ivTTS_ROLE_NANNAN				7			/* 楠楠（童声）。  */
#define ivTTS_ROLE_JINGER				8			/* 婧儿（女声）。  */
#define ivTTS_ROLE_JIAJIA				9			/* 嘉嘉（少女声）。 */
#define ivTTS_ROLE_YUER				    10			/* 玉儿（女声）。  */
#define ivTTS_ROLE_XIAOQIAN			    11			/* 晓倩（女声）。  */
#define ivTTS_ROLE_LAOMA				12			/* 老马（男声）。 */
#define ivTTS_ROLE_BUSH				    13			/* Bush（男声）。 */
#define ivTTS_ROLE_XIAORONG			    14			/* 晓蓉（女声）。  */
#define ivTTS_ROLE_XIAOMEI				15			/* 晓美（女声）。  */
#define ivTTS_ROLE_ANNI					16			/*安妮（女声）。 */
#define ivTTS_ROLE_JOHN					17			/* John（男声）。  */
#define ivTTS_ROLE_ANITA				18			/* Anita（女声）。  */
#define ivTTS_ROLE_TERRY				19			/* Terry（女声）。  */
#define ivTTS_ROLE_CATHERINE			20			/* Catherine（女声）。  */
#define ivTTS_ROLE_TERRYW				21			/* TerryW（女声）。  */
#define ivTTS_ROLE_XIAOLIN				22			/* 晓琳（女声）。 */
#define ivTTS_ROLE_XIAOMENG			    23			/* 晓梦（女声）。  */
#define ivTTS_ROLE_XIAOQIANG			24			/* 小强（男声）。  */
#define ivTTS_ROLE_XIAOKUN			    25			/* 小坤（男声）。  */
#define ivTTS_ROLE_JIUXU				51			/* 许久（男声）。 */
#define ivTTS_ROLE_DUOXU				52			/* 许多（男声）。  */
#define ivTTS_ROLE_XIAOPING			    53			/* 晓萍（女声）。  */
#define ivTTS_ROLE_DONALDDUCK		    54			/* 唐老鸭。  */
#define ivTTS_ROLE_BABYXU				55			/* 许宝宝（童声）。  */
#define ivTTS_ROLE_DALONG				56			/* 大龙（男声）。  */
#define ivTTS_ROLE_TOM					57			/* Tom (male, US English) */
#define ivTTS_ROLE_USER					99			/* 用户自定义。 */
注：资源数据发音人包含中英文晓燕，英文catherine。

4.2.5 发音风格
/* constants for values of parameter ivTTS_PARAM_SPEAK_STYLE */
#define ivTTS_STYLE_PLAIN				0			/* 一字一顿风格。  */
#define ivTTS_STYLE_NORMAL			    1			/* 平铺直叙风格。（默认）  */

4.2.6 语速
/* constants for values of parameter ivTTS_PARAM_VOICE_SPEED */
/* the range of voice speed value is from -32768 to +32767 */
#define ivTTS_SPEED_MIN					-32768		/* slowest voice speed */
#define ivTTS_SPEED_NORMAL			    0			/* normal voice speed (default) */
#define ivTTS_SPEED_MAX				    +32767		/* fastest voice speed */

4.2.7 语调
/* constants for values of parameter ivTTS_PARAM_VOICE_PITCH */
/* the range of voice tone value is from -32768 to +32767 */
#define ivTTS_PITCH_MIN					-32768		/* lowest voice tone */
#define ivTTS_PITCH_NORMAL			    0			/* normal voice tone (default) */
#define ivTTS_PITCH_MAX				    +32767		/* highest voice tone */

4.2.8 音量
/* constants for values of parameter ivTTS_PARAM_VOLUME */
/* the range of volume value is from -32768 to +32767 */
#define ivTTS_VOLUME_MIN				-32768		/* minimized volume */
#define ivTTS_VOLUME_NORMAL			    0			/* normal volume */
#define ivTTS_VOLUME_MAX				+32767		/* maximized volume (default) */

4.2.9 声音特效预置模式
/* constants for values of parameter ivTTS_PARAM_VEMODE */
#define ivTTS_VEMODE_NONE				0			/* 关闭声音特效（默认值）。 */
#define ivTTS_VEMODE_WANDER			    1			/* 忽远忽近。 */
#define ivTTS_VEMODE_ECHO				2			/* 回声。 */
#define ivTTS_VEMODE_ROBERT			    3			/* 机器人。 */
#define ivTTS_VEMODE_CHROUS			    4			/* 合唱。 */
#define ivTTS_VEMODE_UNDERWATER		    5			/* 水下。 */
#define ivTTS_VEMODE_REVERB			    6			/* 混响。 */
#define ivTTS_VEMODE_ECCENTRIC		    7			/* 阴阳怪气。 */

4.2.10 应用场景
/* constants for values of parameter ivTTS_PARAM_USERMODE(ivTTS_PARAM_NAVIGATION_MODE) */
#define ivTTS_USE_NORMAL				0			/* 使用普通模式合成。 */
#define ivTTS_USE_NAVIGATION			1			/*使用导航模式合成。  */
#define ivTTS_USE_MOBILE				2			/*使用手机模式合成。 */
#define ivTTS_USE_EDUCATION			    3			/* 使用教育模式合成。 */
