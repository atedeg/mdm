package dev.atedeg.mdm.productionplanning

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

trait Mocks

class Tests extends AnyFeatureSpec with GivenWhenThen with Matchers with Mocks
