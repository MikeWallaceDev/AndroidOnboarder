package com.chyrta.onboarder;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.chyrta.onboarder.utils.ColorsArrayBuilder;
import com.chyrta.onboarder.views.CircleIndicatorView;

import java.util.List;

@SuppressWarnings("unused")
public abstract class OnboarderActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener
{

	private Integer[]            colors;
	private CircleIndicatorView  circleIndicatorView;
	private ViewPager            vpOnboarderPager;
	private OnboarderAdapter     onboarderAdapter;
	private ImageButton          ibNext;
	private ImageButton          m_imgbtnPrevious;
	private Button               btnSkip;
	private Button               btnFinish;
	private ConstraintLayout     buttonsLayout;
	private FloatingActionButton fab;
	private View                 divider;
	private ArgbEvaluator        evaluator;

	private boolean shouldDarkenButtonsLayout     = false;
	private boolean shouldUseFloatingActionButton = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_onboarder);
		setStatusBackgroundColor();
		hideActionBar();

		circleIndicatorView = (CircleIndicatorView) findViewById(R.id.circle_indicator_view);
		ibNext = (ImageButton) findViewById(R.id.ib_next);
		m_imgbtnPrevious = (ImageButton) findViewById(R.id.imgbtn_previous);
		btnSkip = (Button) findViewById(R.id.btn_skip);
		btnFinish = (Button) findViewById(R.id.btn_finish);
		buttonsLayout = (ConstraintLayout) findViewById(R.id.buttons_layout);
		fab = (FloatingActionButton) findViewById(R.id.fab);
		divider = findViewById(R.id.divider);
		vpOnboarderPager = (ViewPager) findViewById(R.id.vp_onboarder_pager);
		vpOnboarderPager.addOnPageChangeListener(this);

		ibNext.setOnClickListener(this);
		m_imgbtnPrevious.setOnClickListener(this);
		btnSkip.setOnClickListener(this);
		btnFinish.setOnClickListener(this);
		fab.setOnClickListener(this);
		evaluator = new ArgbEvaluator();
	}


	public void setOnboardPagesReady(List<OnboarderPage> pages) {
		onboarderAdapter = new OnboarderAdapter(pages, getSupportFragmentManager());
		vpOnboarderPager.setAdapter(onboarderAdapter);
		colors = ColorsArrayBuilder.getPageBackgroundColors(this, pages);
		circleIndicatorView.setPageIndicators(pages.size());
	}


	public void setInactiveIndicatorColor(int color) {
		circleIndicatorView.setInactiveIndicatorColor(color);
	}


	public void setActiveIndicatorColor(int color) {
		circleIndicatorView.setActiveIndicatorColor(color);
	}


	public void shouldDarkenButtonsLayout(boolean shouldDarkenButtonsLayout_) {
		shouldDarkenButtonsLayout = shouldDarkenButtonsLayout_;
	}


	public void setDividerColor(@ColorInt int color) {
		if (!shouldDarkenButtonsLayout) {
			divider.setBackgroundColor(color);
		}
	}


	public void setDividerHeight(int heightInDp) {
		if (!shouldDarkenButtonsLayout) {
			divider.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
		}
	}


	public void setDividerVisibility(int dividerVisibility) {
		divider.setVisibility(dividerVisibility);
	}


	public void setSkipButtonTitle(CharSequence title) {
		btnSkip.setText(title);
	}


	public void setSkipButtonHidden() {
		btnSkip.setVisibility(View.INVISIBLE);
	}


	public void setSkipButtonTitle(@StringRes int titleResId) {
		btnSkip.setText(titleResId);
	}


	public void setFinishButtonTitle(CharSequence title) {
		btnFinish.setText(title);
	}


	public void setFinishButtonTitle(@StringRes int titleResId) {
		btnFinish.setText(titleResId);
	}


	public void shouldUseFloatingActionButton(boolean shouldUseFloatingActionButton_) {

		shouldUseFloatingActionButton = shouldUseFloatingActionButton_;

		if (shouldUseFloatingActionButton) {
			fab.setVisibility(View.VISIBLE);
			setDividerVisibility(View.INVISIBLE);
			shouldDarkenButtonsLayout(false);
			btnFinish.setVisibility(View.INVISIBLE);
			btnSkip.setVisibility(View.INVISIBLE);
			ibNext.setVisibility(View.INVISIBLE);
			ibNext.setFocusable(false);
			m_imgbtnPrevious.setVisibility(View.INVISIBLE);
			m_imgbtnPrevious.setFocusable(false);
			buttonsLayout.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, getResources().getDisplayMetrics());
		}
	}


	private int darkenColor(@ColorInt int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.9f;
		return Color.HSVToColor(hsv);
	}


	public void setStatusBackgroundColor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
			getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_transparent));
		}
	}


	@Override
	public void onClick(View v) {
		final int i = v.getId();
		final boolean isInLastPage = vpOnboarderPager.getCurrentItem() == onboarderAdapter.getCount() - 1;

		if (i == R.id.ib_next || i == R.id.fab && !isInLastPage) {
			vpOnboarderPager.setCurrentItem(vpOnboarderPager.getCurrentItem() + 1);
		}
		else if (i == R.id.imgbtn_previous) {
			vpOnboarderPager.setCurrentItem(vpOnboarderPager.getCurrentItem() - 1);
		}
		else if (i == R.id.btn_skip) {
			onSkipButtonPressed();
		}
		else if (i == R.id.btn_finish || i == R.id.fab && isInLastPage) {
			onFinishButtonPressed();
		}
	}


	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (position < (onboarderAdapter.getCount() - 1) && position < (colors.length - 1)) {
			vpOnboarderPager.setBackgroundColor((Integer) evaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
			if (shouldDarkenButtonsLayout) {
				buttonsLayout.setBackgroundColor(darkenColor((Integer) evaluator.evaluate(positionOffset, colors[position], colors[position + 1])));
				divider.setVisibility(View.INVISIBLE);
			}
		}
		else {
			vpOnboarderPager.setBackgroundColor(colors[colors.length - 1]);
			if (shouldDarkenButtonsLayout) {
				buttonsLayout.setBackgroundColor(darkenColor(colors[colors.length - 1]));
				divider.setVisibility(View.INVISIBLE);
			}
		}
	}


	@Override
	public void onPageSelected(int position) {
		final int lastPagePosition = onboarderAdapter.getCount() - 1;
		circleIndicatorView.setCurrentPage(position);

		// Grey out the previous button when on the first page
		m_imgbtnPrevious.setColorFilter(position == 0 && !shouldUseFloatingActionButton ? Color.GRAY : Color.WHITE);

		// Hide the next button and the skip button when on the last page (otherwise : show)
		ibNext.setVisibility(position == lastPagePosition && !shouldUseFloatingActionButton ? View.INVISIBLE : View.VISIBLE);
		btnSkip.setVisibility(position == lastPagePosition && !shouldUseFloatingActionButton ? View.INVISIBLE : View.VISIBLE);

		// Show the Finish button when on the last page.  (otherwise : hide)
		btnFinish.setVisibility(position == lastPagePosition && !shouldUseFloatingActionButton ? View.VISIBLE : View.INVISIBLE);

		if (shouldUseFloatingActionButton) {
			fab.setImageResource(position == lastPagePosition ? R.drawable.ic_done_white_24dp : R.drawable.ic_arrow_forward_white_24dp);
		}
	}


	@Override
	public void onPageScrollStateChanged(int state) {
	}


	private void hideActionBar() {
		if (getSupportActionBar() != null) {
			getSupportActionBar().hide();
		}
	}


	protected void onSkipButtonPressed() {
		vpOnboarderPager.setCurrentItem(onboarderAdapter.getCount());
	}


	abstract public void onFinishButtonPressed();
}
