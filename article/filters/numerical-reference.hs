{-# LANGUAGE PatternGuards #-}

{- This filter allows for numerical section references. It should be
used with "--number-sections", since it uses a similar numbering
scheme. It works by using the link notation: given a header with a
given id, we can refer to that number by using a link with `#` in it:

    My Header {#my-header-id}
    =========

    This refers back the [Section #](#my-header-id)

It takes two metadata options, which can either be specified
in YAML or on the command-line:

  * `ref-num-sign`: This is the sign that will be used to indicate
    numerical reference. It defaults to `#`.

  * `ref-num-unlink`: Whether to make the the link a plain string, or
    keep it a link. The default is false.
-}

import Text.Pandoc
import Text.Pandoc.JSON
import Text.Pandoc.Walk
import Text.Pandoc.Shared
import Control.Monad.State
import Control.Monad.Reader
import qualified Data.Map as M
import Data.Maybe
import Data.List
import Control.Monad.Reader
import Control.Monad.State
import Control.Applicative ((<$>))

data ScriptConfig = ScriptConfig { confNumSign :: String
                                 , confUnlinkNum :: Bool
                                 }

getConfig :: Meta -> ScriptConfig
getConfig meta = ScriptConfig { confNumSign = num
                              , confUnlinkNum = bool}
  where num = case lookupMeta "ref-num-sign" meta of
          Just (MetaString s) -> s
          Just (MetaInlines ils) -> concatMap stringify ils
          _                      -> "#" --  default to number sign
        bool = case lookupMeta "ref-num-unlink" meta of
          Just (MetaBool b) -> b
          _                 -> False

data ScriptState = ScriptState { stCounter :: Counter
                               , stNumMap :: M.Map String Counter
                               }

defaultState :: ScriptState
defaultState = ScriptState [] M.empty

type MyMonad = ReaderT ScriptConfig (State ScriptState)

type Counter = [Int]

incCounter :: Int -> Counter -> Counter
incCounter lvl cntr
  | lvl < 1 = cntr
  | otherwise =
      let xs = take (lvl - 1) $ cntr ++ (repeat 0)
          (y:ys) = case drop (lvl - 1) cntr of
            [] -> [0]
            l  -> l
      in
       xs ++ ((y + 1) : ys)

getSecNumber :: Block -> MyMonad Block
getSecNumber b@(Header n (ident, classes, _) _)
  | notElem "unnumbered" classes = do
      cntr <- gets stCounter
      mp <- gets stNumMap
      let cntr' = incCounter n cntr
      put $ ScriptState { stCounter = cntr'
                        , stNumMap = M.insert ident cntr' mp}
      return b
getSecNumber b = return b

printCntr :: Counter -> String
printCntr cntr = concat $ intersperse "." $ map show cntr

convertInlines :: String -> String -> [Inline] -> [Inline]
convertInlines orig repl ils =
  let f (Str s) | s == orig = (Str repl)
      f i                   = i
  in
   map f ils

insertNumbers :: Inline -> MyMonad [Inline]
insertNumbers il@(Link ils target)
  | ('#':anchor, _) <- target = do
    numSign <- asks confNumSign
    unLink <- asks confUnlinkNum
    mp <- gets stNumMap
    return $ case filter ((Str numSign) ==) ils of
      [] -> [il]
      _  -> case M.lookup anchor mp of
        Just cntr | unLink -> convertInlines numSign (printCntr cntr) ils
                  | otherwise ->
                      [Link (convertInlines numSign (printCntr cntr) ils) target]
        Nothing   -> [il]
insertNumbers il = return [il]

-- | The 'concatMapM' function generalizes 'concatMap' to arbitrary monads.
concatMapM        :: (Monad m) => (a -> m [b]) -> [a] -> m [b]
concatMapM f xs   =  liftM concat (mapM f xs)

run' :: [Block] -> MyMonad [Block]
run' blks = do
  blks' <- mapM getSecNumber blks
  bottomUpM (concatMapM insertNumbers) blks'

run :: Pandoc -> Pandoc
run (Pandoc meta blks) = Pandoc meta blks'
  where blks' = evalState (runReaderT (run' blks) config) defaultState
        config = getConfig meta

main = toJSONFilter run

