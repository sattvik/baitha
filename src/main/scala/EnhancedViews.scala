package com.sattvik.baitha

import android.app.Activity
import android.view._

trait EnhancedViews {
  class EnhancedView(view: View) {
    def onClick(f: View => Unit) {
      view.setOnClickListener(new View.OnClickListener {
          override def onClick(v: View) = f(v)
        })
    }
  }

  implicit def viewToEnhancedView(view: View) =
      new EnhancedView(view)
}
