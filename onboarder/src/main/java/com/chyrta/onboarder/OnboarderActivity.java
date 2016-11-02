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

	private Integer[]            m_colors;
	private CircleIndicatorView  m_circleIndicatorView;
	private ViewPager            m_vwpgrOnboarder;
	private OnboarderAdapter     m_onboarderAdapter;
	private ImageButton          m_imgbtnNext;
	private ImageButton          m_imgbtnPrevious;
	private Button               m_btnSkip;
	private Button               m_btnFinish;
	private ConstraintLayout     m_conlytButtons;
	private FloatingActionButton m_fab;
	private View                 m_vwDivider;
	private ArgbEvaluator        m_evaluator;
	private boolean m_darkenButtonLayout      = false;
	private boolean m_useFloatingActionButton = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_onboarder);

		setStatusBackgroundColor();
		hideActionBar();

		findViews();

		setupViews();
	}


	public void setOnboardPagesReady(List<OnboarderPage> pages) {
		m_onboarderAdapter = new OnboarderAdapter(pages, getSupportFragmentManager());
		m_vwpgrOnboarder.setAdapter(m_onboarderAdapter);
		m_colors = ColorsArrayBuilder.getPageBackgroundColors(this, pages);
		m_circleIndicatorView.setPageIndicators(pages.size());
	}


	public void setInactiveIndicatorColor(int color) {
		m_circleIndicatorView.setInactiveIndicatorColor(color);
	}


	public void setActiveIndicatorColor(int color) {
		m_circleIndicatorView.setActiveIndicatorColor(color);
	}


	public void darkenButtonLayout(boolean darkenButtonLayout) {
		m_darkenButtonLayout = darkenButtonLayout;
	}


	public void setDividerColor(@ColorInt int color) {
		if (!m_darkenButtonLayout) {
			m_vwDivider.setBackgroundColor(color);
		}
	}


	public void setDividerHeight(int heightInDp) {
		if (!m_darkenButtonLayout) {
			m_vwDivider.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
		}
	}


	public void setDividerVisibility(int dividerVisibility) {
		m_vwDivider.setVisibility(dividerVisibility);
	}


	public void setSkipButtonTitle(CharSequence title) {
		m_btnSkip.setText(title);
	}


	public void setSkipButtonTitle(@StringRes int titleResId) {
		m_btnSkip.setText(titleResId);
	}


	public void setSkipButtonHidden() {
		m_btnSkip.setVisibility(View.INVISIBLE);
	}


	public void setFinishButtonTitle(CharSequence title) {
		m_btnFinish.setText(title);
	}


	public void setFinishButtonTitle(@StringRes int titleResId) {
		m_btnFinish.setText(titleResId);
	}


	public void setStatusBackgroundColor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
			getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_transparent));
		}
	}


	public void useFloatingActionButton() {
		m_useFloatingActionButton = true;

		// show the FAB
		m_fab.setVisibility(View.VISIBLE);

		setDividerVisibility(View.INVISIBLE);
		darkenButtonLayout(false);

		m_btnFinish.setVisibility(View.INVISIBLE);
		m_btnSkip.setVisibility(View.INVISIBLE);
		m_imgbtnNext.setVisibility(View.INVISIBLE);
		m_imgbtnNext.setFocusable(false);
		m_imgbtnPrevious.setVisibility(View.INVISIBLE);
		m_imgbtnPrevious.setFocusable(false);

		m_conlytButtons.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, getResources().getDisplayMetrics());
	}


	@Override
	public void onClick(View v) {
		final int i = v.getId();
		final boolean isInLastPage = m_vwpgrOnboarder.getCurrentItem() == m_onboarderAdapter.getCount() - 1;

		if (i == R.id.ib_next || i == R.id.fab && !isInLastPage) {
			m_vwpgrOnboarder.setCurrentItem(m_vwpgrOnboarder.getCurrentItem() + 1);
		}
		else if (i == R.id.imgbtn_previous) {
			m_vwpgrOnboarder.setCurrentItem(m_vwpgrOnboarder.getCurrentItem() - 1);
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
		int backgroundColor;

		if (position < (m_onboarderAdapter.getCount() - 1) && position < (m_colors.length - 1)) {
			backgroundColor =(Integer) m_evaluator.evaluate(positionOffset, m_colors[position], m_colors[position + 1]) ;
		}
		else {
			backgroundColor = m_colors[m_colors.length - 1];
		}

		m_vwpgrOnboarder.setBackgroundColor(backgroundColor);

		if (m_darkenButtonLayout) {
			m_conlytButtons.setBackgroundColor(darkenColor(backgroundColor));
			m_vwDivider.setVisibility(View.INVISIBLE);
		}
	}


	@Override
	public void onPageSelected(int position) {
		final int lastPagePosition = m_onboarderAdapter.getCount() - 1;

		m_circleIndicatorView.setCurrentPage(position);

		// Grey out the previous button when on the first page
		m_imgbtnPrevious.setColorFilter(position == 0 && !m_useFloatingActionButton ? Color.GRAY : Color.WHITE);

		// Hide the next button and the skip button when on the last page (otherwise : show)
		m_imgbtnNext.setVisibility(position == lastPagePosition && !m_useFloatingActionButton ? View.INVISIBLE : View.VISIBLE);
		m_btnSkip.setVisibility(position == lastPagePosition && !m_useFloatingActionButton ? View.INVISIBLE : View.VISIBLE);

		// Show the Finish button when on the last page.  (otherwise : hide)
		m_btnFinish.setVisibility(position == lastPagePosition && !m_useFloatingActionButton ? View.VISIBLE : View.INVISIBLE);

		if (m_useFloatingActionButton) {
			m_fab.setImageResource(position == lastPagePosition ? R.drawable.ic_done_white_24dp : R.drawable.ic_arrow_forward_white_24dp);
		}
	}


	@Override
	public void onPageScrollStateChanged(int state) {
	}


	protected void onSkipButtonPressed() {
		m_vwpgrOnboarder.setCurrentItem(m_onboarderAdapter.getCount());
	}


	abstract protected void onFinishButtonPressed();


	private void findViews() {
		m_circleIndicatorView = (CircleIndicatorView) findViewById(R.id.circle_indicator_view);
		m_imgbtnNext = (ImageButton) findViewById(R.id.ib_next);
		m_imgbtnPrevious = (ImageButton) findViewById(R.id.imgbtn_previous);
		m_btnSkip = (Button) findViewById(R.id.btn_skip);
		m_btnFinish = (Button) findViewById(R.id.btn_finish);
		m_conlytButtons = (ConstraintLayout) findViewById(R.id.buttons_layout);
		m_fab = (FloatingActionButton) findViewById(R.id.fab);
		m_vwDivider = findViewById(R.id.divider);
		m_vwpgrOnboarder = (ViewPager) findViewById(R.id.vp_onboarder_pager);
	}


	private void setupViews() {
		m_vwpgrOnboarder.addOnPageChangeListener(this);
		m_imgbtnNext.setOnClickListener(this);
		m_imgbtnPrevious.setOnClickListener(this);
		m_btnSkip.setOnClickListener(this);
		m_btnFinish.setOnClickListener(this);
		m_fab.setOnClickListener(this);
		m_evaluator = new ArgbEvaluator();
	}


	private int darkenColor(@ColorInt int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.9f;
		return Color.HSVToColor(hsv);
	}


	private void hideActionBar() {
		if (getSupportActionBar() != null) {
			getSupportActionBar().hide();
		}
	}
}
