package dev.atedeg.mdm.production

import cats.Monad
import dev.atedeg.mdm.utils.{CanRaise, Emits}
import OutgoingEvent.*

// TODO: Does this action emits a "startProduction" event that is used by the machines? Or does it
//       communicate in a different way with the smart machines in order to start the production?
/**
 * Starts a [[Production.ToStart production]] by calculating the quintals of [[Ingredient ingredients]] needed to
 * produce the specified [[Product product]]; the [[Ingredient ingredients]] needed are specified
 * by the [[Recipe recipe]] read from a [[RecipeBook recipe book]].
 */
def startProduction[M[_]: Monad: CanRaise[MissingRecipe]: Emits[IngredientUsed]]
  (recipeBook: RecipeBook)
  (production: Production.ToStart): Production.InProgress = ???

/**
 * Ends a [[Production.InProgress production]] by assigning it a [[LotNumber lot number]].
 */
def endProduction[M[_]: Monad: Emits[ProductionEnded]]
  (production: Production.InProgress): Production.Ended = ???