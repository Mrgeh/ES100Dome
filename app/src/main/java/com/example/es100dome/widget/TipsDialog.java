package com.example.es100dome.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.es100.MyState;
import com.example.es100dome.R;

/**
 * 自定义AlertDialog
 * @author rcm
 *
 */
public class TipsDialog extends Dialog {

	private MyState sta = MyState.getInstance();

	
	public TipsDialog(Context context, int theme) {
		super(context, theme);
	}

	public TipsDialog(Context context) {
		super(context);
	}
	
	public void setMessage(String msg) {
		Builder.mTextViewMsg.setText(msg);
	}
	
	public void setMessage(int msg) {
		Builder.mTextViewMsg.setText(msg);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private String neutralButtonText;
		private int imageResId;
		private View contentView;
		
		private static TextView mTextViewMsg;

		private OnClickListener positiveButtonClickListener,
				negativeButtonClickListener,
				neutralButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setImage(int resId) {
			this.imageResId = resId;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNeutralButton(int neutralButtonText,
				OnClickListener listener) {
			this.neutralButtonText = (String) context
					.getText(neutralButtonText);
			this.neutralButtonClickListener = listener;
			return this;
		}

		public Builder setNeutralButton(String neutralButtonText,
				OnClickListener listener) {
			this.neutralButtonText = neutralButtonText;
			this.neutralButtonClickListener = listener;
			return this;
		}
		/**
		 * Create the custom dialog
		 */
		public TipsDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final TipsDialog dialog = new TipsDialog(context,
					R.style.TipsDialog);
			dialog.setCanceledOnTouchOutside(true);
			View layout = inflater.inflate(R.layout.tips_dialog, null);
			//dialog.addContentView(layout, new LayoutParams(
			//		LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			((TextView) layout.findViewById(R.id.title)).setText(title);
			if(imageResId != 0) {
				((ImageView) layout.findViewById(R.id.content_icon)).setImageResource(imageResId);
			} else {
				((ImageView) layout.findViewById(R.id.content_icon)).setVisibility(View.GONE);
			}
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			if (neutralButtonText != null) {
				((Button) layout.findViewById(R.id.neutralButton))
						.setText(neutralButtonText);
				if (neutralButtonClickListener != null) {
					((Button) layout.findViewById(R.id.neutralButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									neutralButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEUTRAL);
								}
							});
				}
			} else {
				layout.findViewById(R.id.neutralButton).setVisibility(
						View.GONE);
			}
			if (message != null) {
//				((TextView) layout.findViewById(R.id.message)).setText(message);
				mTextViewMsg = (TextView)layout.findViewById(R.id.message);
				mTextViewMsg.setText(message);
			} else if (contentView != null) {
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}

}