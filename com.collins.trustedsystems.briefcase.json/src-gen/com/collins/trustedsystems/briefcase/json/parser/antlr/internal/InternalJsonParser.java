package com.collins.trustedsystems.briefcase.json.parser.antlr.internal; 

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.collins.trustedsystems.briefcase.json.services.JsonGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalJsonParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "Classifier", "Reference", "Constant", "Applies", "Binding", "Compute", "Delta", "False", "Modes", "Null", "True", "PlusSignEqualsSignGreaterThanSign", "FullStopFullStop", "ColonColon", "EqualsSignGreaterThanSign", "In", "To", "LeftParenthesis", "RightParenthesis", "Asterisk", "PlusSign", "Comma", "HyphenMinus", "FullStop", "Colon", "Semicolon", "LeftSquareBracket", "RightSquareBracket", "LeftCurlyBracket", "RightCurlyBracket", "RULE_INT_NUMBER", "RULE_REAL_NUMBER", "RULE_STRING", "RULE_SL_COMMENT", "RULE_DIGIT", "RULE_EXPONENT", "RULE_INT_EXPONENT", "RULE_REAL_LIT", "RULE_BASED_INTEGER", "RULE_INTEGER_LIT", "RULE_EXTENDED_DIGIT", "RULE_ID", "RULE_WS"
    };
    public static final int Null=13;
    public static final int EqualsSignGreaterThanSign=18;
    public static final int True=14;
    public static final int False=11;
    public static final int RULE_REAL_NUMBER=35;
    public static final int RULE_INT_EXPONENT=40;
    public static final int PlusSignEqualsSignGreaterThanSign=15;
    public static final int LeftParenthesis=21;
    public static final int FullStopFullStop=16;
    public static final int To=20;
    public static final int Applies=7;
    public static final int RULE_BASED_INTEGER=42;
    public static final int RightSquareBracket=31;
    public static final int Binding=8;
    public static final int RULE_ID=45;
    public static final int RightParenthesis=22;
    public static final int RULE_DIGIT=38;
    public static final int ColonColon=17;
    public static final int PlusSign=24;
    public static final int LeftSquareBracket=30;
    public static final int RULE_INTEGER_LIT=43;
    public static final int In=19;
    public static final int Constant=6;
    public static final int RULE_INT_NUMBER=34;
    public static final int RULE_REAL_LIT=41;
    public static final int RULE_STRING=36;
    public static final int Classifier=4;
    public static final int RULE_SL_COMMENT=37;
    public static final int Comma=25;
    public static final int HyphenMinus=26;
    public static final int Colon=28;
    public static final int RightCurlyBracket=33;
    public static final int EOF=-1;
    public static final int Asterisk=23;
    public static final int Modes=12;
    public static final int FullStop=27;
    public static final int RULE_WS=46;
    public static final int Reference=5;
    public static final int LeftCurlyBracket=32;
    public static final int Semicolon=29;
    public static final int RULE_EXPONENT=39;
    public static final int Delta=10;
    public static final int Compute=9;
    public static final int RULE_EXTENDED_DIGIT=44;

    // delegates
    // delegators


        public InternalJsonParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalJsonParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalJsonParser.tokenNames; }
    public String getGrammarFileName() { return "InternalJsonParser.g"; }




    	private JsonGrammarAccess grammarAccess;
    	 	
    	public InternalJsonParser(TokenStream input, JsonGrammarAccess grammarAccess) {
    		this(input);
    		this.grammarAccess = grammarAccess;
    		registerRules(grammarAccess.getGrammar());
    	}
    	
    	@Override
    	protected String getFirstRuleName() {
    		return "AnnexLibrary";	
    	} 
    	   	   	
    	@Override
    	protected JsonGrammarAccess getGrammarAccess() {
    		return grammarAccess;
    	}



    // $ANTLR start "entryRuleAnnexLibrary"
    // InternalJsonParser.g:61:1: entryRuleAnnexLibrary returns [EObject current=null] : iv_ruleAnnexLibrary= ruleAnnexLibrary EOF ;
    public final EObject entryRuleAnnexLibrary() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAnnexLibrary = null;


        try {
            // InternalJsonParser.g:62:2: (iv_ruleAnnexLibrary= ruleAnnexLibrary EOF )
            // InternalJsonParser.g:63:2: iv_ruleAnnexLibrary= ruleAnnexLibrary EOF
            {
             newCompositeNode(grammarAccess.getAnnexLibraryRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAnnexLibrary=ruleAnnexLibrary();

            state._fsp--;

             current =iv_ruleAnnexLibrary; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAnnexLibrary"


    // $ANTLR start "ruleAnnexLibrary"
    // InternalJsonParser.g:70:1: ruleAnnexLibrary returns [EObject current=null] : this_JsonAnnexLibrary_0= ruleJsonAnnexLibrary ;
    public final EObject ruleAnnexLibrary() throws RecognitionException {
        EObject current = null;

        EObject this_JsonAnnexLibrary_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:73:28: (this_JsonAnnexLibrary_0= ruleJsonAnnexLibrary )
            // InternalJsonParser.g:75:5: this_JsonAnnexLibrary_0= ruleJsonAnnexLibrary
            {
             
                    newCompositeNode(grammarAccess.getAnnexLibraryAccess().getJsonAnnexLibraryParserRuleCall()); 
                
            pushFollow(FollowSets000.FOLLOW_2);
            this_JsonAnnexLibrary_0=ruleJsonAnnexLibrary();

            state._fsp--;


                    current = this_JsonAnnexLibrary_0;
                    afterParserOrEnumRuleCall();
                

            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAnnexLibrary"


    // $ANTLR start "entryRuleJsonAnnexLibrary"
    // InternalJsonParser.g:93:1: entryRuleJsonAnnexLibrary returns [EObject current=null] : iv_ruleJsonAnnexLibrary= ruleJsonAnnexLibrary EOF ;
    public final EObject entryRuleJsonAnnexLibrary() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleJsonAnnexLibrary = null;


        try {
            // InternalJsonParser.g:94:2: (iv_ruleJsonAnnexLibrary= ruleJsonAnnexLibrary EOF )
            // InternalJsonParser.g:95:2: iv_ruleJsonAnnexLibrary= ruleJsonAnnexLibrary EOF
            {
             newCompositeNode(grammarAccess.getJsonAnnexLibraryRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonAnnexLibrary=ruleJsonAnnexLibrary();

            state._fsp--;

             current =iv_ruleJsonAnnexLibrary; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonAnnexLibrary"


    // $ANTLR start "ruleJsonAnnexLibrary"
    // InternalJsonParser.g:102:1: ruleJsonAnnexLibrary returns [EObject current=null] : ( () ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )? ) ;
    public final EObject ruleJsonAnnexLibrary() throws RecognitionException {
        EObject current = null;

        EObject lv_jsonAnnexElement_1_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:105:28: ( ( () ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )? ) )
            // InternalJsonParser.g:106:1: ( () ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )? )
            {
            // InternalJsonParser.g:106:1: ( () ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )? )
            // InternalJsonParser.g:106:2: () ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )?
            {
            // InternalJsonParser.g:106:2: ()
            // InternalJsonParser.g:107:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getJsonAnnexLibraryAccess().getJsonAnnexLibraryAction_0(),
                        current);
                

            }

            // InternalJsonParser.g:112:2: ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==False||(LA1_0>=Null && LA1_0<=True)||LA1_0==LeftSquareBracket||LA1_0==LeftCurlyBracket||(LA1_0>=RULE_INT_NUMBER && LA1_0<=RULE_STRING)) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // InternalJsonParser.g:113:1: (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement )
                    {
                    // InternalJsonParser.g:113:1: (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement )
                    // InternalJsonParser.g:114:3: lv_jsonAnnexElement_1_0= ruleJsonAnnexElement
                    {
                     
                    	        newCompositeNode(grammarAccess.getJsonAnnexLibraryAccess().getJsonAnnexElementJsonAnnexElementParserRuleCall_1_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_jsonAnnexElement_1_0=ruleJsonAnnexElement();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getJsonAnnexLibraryRule());
                    	        }
                           		set(
                           			current, 
                           			"jsonAnnexElement",
                            		lv_jsonAnnexElement_1_0, 
                            		"com.collins.trustedsystems.briefcase.json.Json.JsonAnnexElement");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonAnnexLibrary"


    // $ANTLR start "entryRuleJsonAnnexSubclause"
    // InternalJsonParser.g:138:1: entryRuleJsonAnnexSubclause returns [EObject current=null] : iv_ruleJsonAnnexSubclause= ruleJsonAnnexSubclause EOF ;
    public final EObject entryRuleJsonAnnexSubclause() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleJsonAnnexSubclause = null;


        try {
            // InternalJsonParser.g:139:2: (iv_ruleJsonAnnexSubclause= ruleJsonAnnexSubclause EOF )
            // InternalJsonParser.g:140:2: iv_ruleJsonAnnexSubclause= ruleJsonAnnexSubclause EOF
            {
             newCompositeNode(grammarAccess.getJsonAnnexSubclauseRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonAnnexSubclause=ruleJsonAnnexSubclause();

            state._fsp--;

             current =iv_ruleJsonAnnexSubclause; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonAnnexSubclause"


    // $ANTLR start "ruleJsonAnnexSubclause"
    // InternalJsonParser.g:147:1: ruleJsonAnnexSubclause returns [EObject current=null] : ( () ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )? ) ;
    public final EObject ruleJsonAnnexSubclause() throws RecognitionException {
        EObject current = null;

        EObject lv_jsonAnnexElement_1_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:150:28: ( ( () ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )? ) )
            // InternalJsonParser.g:151:1: ( () ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )? )
            {
            // InternalJsonParser.g:151:1: ( () ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )? )
            // InternalJsonParser.g:151:2: () ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )?
            {
            // InternalJsonParser.g:151:2: ()
            // InternalJsonParser.g:152:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getJsonAnnexSubclauseAccess().getJsonAnnexSubclauseAction_0(),
                        current);
                

            }

            // InternalJsonParser.g:157:2: ( (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==False||(LA2_0>=Null && LA2_0<=True)||LA2_0==LeftSquareBracket||LA2_0==LeftCurlyBracket||(LA2_0>=RULE_INT_NUMBER && LA2_0<=RULE_STRING)) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // InternalJsonParser.g:158:1: (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement )
                    {
                    // InternalJsonParser.g:158:1: (lv_jsonAnnexElement_1_0= ruleJsonAnnexElement )
                    // InternalJsonParser.g:159:3: lv_jsonAnnexElement_1_0= ruleJsonAnnexElement
                    {
                     
                    	        newCompositeNode(grammarAccess.getJsonAnnexSubclauseAccess().getJsonAnnexElementJsonAnnexElementParserRuleCall_1_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_jsonAnnexElement_1_0=ruleJsonAnnexElement();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getJsonAnnexSubclauseRule());
                    	        }
                           		set(
                           			current, 
                           			"jsonAnnexElement",
                            		lv_jsonAnnexElement_1_0, 
                            		"com.collins.trustedsystems.briefcase.json.Json.JsonAnnexElement");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonAnnexSubclause"


    // $ANTLR start "entryRuleJsonAnnexElement"
    // InternalJsonParser.g:183:1: entryRuleJsonAnnexElement returns [EObject current=null] : iv_ruleJsonAnnexElement= ruleJsonAnnexElement EOF ;
    public final EObject entryRuleJsonAnnexElement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleJsonAnnexElement = null;


        try {
            // InternalJsonParser.g:184:2: (iv_ruleJsonAnnexElement= ruleJsonAnnexElement EOF )
            // InternalJsonParser.g:185:2: iv_ruleJsonAnnexElement= ruleJsonAnnexElement EOF
            {
             newCompositeNode(grammarAccess.getJsonAnnexElementRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonAnnexElement=ruleJsonAnnexElement();

            state._fsp--;

             current =iv_ruleJsonAnnexElement; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonAnnexElement"


    // $ANTLR start "ruleJsonAnnexElement"
    // InternalJsonParser.g:192:1: ruleJsonAnnexElement returns [EObject current=null] : (this_JsonAnnexObject_0= ruleJsonAnnexObject | this_JsonAnnexArray_1= ruleJsonAnnexArray | this_JsonAnnexString_2= ruleJsonAnnexString | this_JsonAnnexNumber_3= ruleJsonAnnexNumber | this_JsonAnnexBoolean_4= ruleJsonAnnexBoolean | this_JsonAnnexNull_5= ruleJsonAnnexNull ) ;
    public final EObject ruleJsonAnnexElement() throws RecognitionException {
        EObject current = null;

        EObject this_JsonAnnexObject_0 = null;

        EObject this_JsonAnnexArray_1 = null;

        EObject this_JsonAnnexString_2 = null;

        EObject this_JsonAnnexNumber_3 = null;

        EObject this_JsonAnnexBoolean_4 = null;

        EObject this_JsonAnnexNull_5 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:195:28: ( (this_JsonAnnexObject_0= ruleJsonAnnexObject | this_JsonAnnexArray_1= ruleJsonAnnexArray | this_JsonAnnexString_2= ruleJsonAnnexString | this_JsonAnnexNumber_3= ruleJsonAnnexNumber | this_JsonAnnexBoolean_4= ruleJsonAnnexBoolean | this_JsonAnnexNull_5= ruleJsonAnnexNull ) )
            // InternalJsonParser.g:196:1: (this_JsonAnnexObject_0= ruleJsonAnnexObject | this_JsonAnnexArray_1= ruleJsonAnnexArray | this_JsonAnnexString_2= ruleJsonAnnexString | this_JsonAnnexNumber_3= ruleJsonAnnexNumber | this_JsonAnnexBoolean_4= ruleJsonAnnexBoolean | this_JsonAnnexNull_5= ruleJsonAnnexNull )
            {
            // InternalJsonParser.g:196:1: (this_JsonAnnexObject_0= ruleJsonAnnexObject | this_JsonAnnexArray_1= ruleJsonAnnexArray | this_JsonAnnexString_2= ruleJsonAnnexString | this_JsonAnnexNumber_3= ruleJsonAnnexNumber | this_JsonAnnexBoolean_4= ruleJsonAnnexBoolean | this_JsonAnnexNull_5= ruleJsonAnnexNull )
            int alt3=6;
            switch ( input.LA(1) ) {
            case LeftCurlyBracket:
                {
                alt3=1;
                }
                break;
            case LeftSquareBracket:
                {
                alt3=2;
                }
                break;
            case RULE_STRING:
                {
                alt3=3;
                }
                break;
            case RULE_INT_NUMBER:
            case RULE_REAL_NUMBER:
                {
                alt3=4;
                }
                break;
            case False:
            case True:
                {
                alt3=5;
                }
                break;
            case Null:
                {
                alt3=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // InternalJsonParser.g:197:5: this_JsonAnnexObject_0= ruleJsonAnnexObject
                    {
                     
                            newCompositeNode(grammarAccess.getJsonAnnexElementAccess().getJsonAnnexObjectParserRuleCall_0()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_JsonAnnexObject_0=ruleJsonAnnexObject();

                    state._fsp--;


                            current = this_JsonAnnexObject_0;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalJsonParser.g:207:5: this_JsonAnnexArray_1= ruleJsonAnnexArray
                    {
                     
                            newCompositeNode(grammarAccess.getJsonAnnexElementAccess().getJsonAnnexArrayParserRuleCall_1()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_JsonAnnexArray_1=ruleJsonAnnexArray();

                    state._fsp--;


                            current = this_JsonAnnexArray_1;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // InternalJsonParser.g:217:5: this_JsonAnnexString_2= ruleJsonAnnexString
                    {
                     
                            newCompositeNode(grammarAccess.getJsonAnnexElementAccess().getJsonAnnexStringParserRuleCall_2()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_JsonAnnexString_2=ruleJsonAnnexString();

                    state._fsp--;


                            current = this_JsonAnnexString_2;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // InternalJsonParser.g:227:5: this_JsonAnnexNumber_3= ruleJsonAnnexNumber
                    {
                     
                            newCompositeNode(grammarAccess.getJsonAnnexElementAccess().getJsonAnnexNumberParserRuleCall_3()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_JsonAnnexNumber_3=ruleJsonAnnexNumber();

                    state._fsp--;


                            current = this_JsonAnnexNumber_3;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 5 :
                    // InternalJsonParser.g:237:5: this_JsonAnnexBoolean_4= ruleJsonAnnexBoolean
                    {
                     
                            newCompositeNode(grammarAccess.getJsonAnnexElementAccess().getJsonAnnexBooleanParserRuleCall_4()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_JsonAnnexBoolean_4=ruleJsonAnnexBoolean();

                    state._fsp--;


                            current = this_JsonAnnexBoolean_4;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 6 :
                    // InternalJsonParser.g:247:5: this_JsonAnnexNull_5= ruleJsonAnnexNull
                    {
                     
                            newCompositeNode(grammarAccess.getJsonAnnexElementAccess().getJsonAnnexNullParserRuleCall_5()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_JsonAnnexNull_5=ruleJsonAnnexNull();

                    state._fsp--;


                            current = this_JsonAnnexNull_5;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonAnnexElement"


    // $ANTLR start "entryRuleJsonAnnexObject"
    // InternalJsonParser.g:263:1: entryRuleJsonAnnexObject returns [EObject current=null] : iv_ruleJsonAnnexObject= ruleJsonAnnexObject EOF ;
    public final EObject entryRuleJsonAnnexObject() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleJsonAnnexObject = null;


        try {
            // InternalJsonParser.g:264:2: (iv_ruleJsonAnnexObject= ruleJsonAnnexObject EOF )
            // InternalJsonParser.g:265:2: iv_ruleJsonAnnexObject= ruleJsonAnnexObject EOF
            {
             newCompositeNode(grammarAccess.getJsonAnnexObjectRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonAnnexObject=ruleJsonAnnexObject();

            state._fsp--;

             current =iv_ruleJsonAnnexObject; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonAnnexObject"


    // $ANTLR start "ruleJsonAnnexObject"
    // InternalJsonParser.g:272:1: ruleJsonAnnexObject returns [EObject current=null] : ( () otherlv_1= LeftCurlyBracket ( ( (lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember ) ) (otherlv_3= Comma ( (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember ) ) )* )? otherlv_5= RightCurlyBracket ) ;
    public final EObject ruleJsonAnnexObject() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_jsonAnnexMembers_2_0 = null;

        EObject lv_jsonAnnexMembers_4_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:275:28: ( ( () otherlv_1= LeftCurlyBracket ( ( (lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember ) ) (otherlv_3= Comma ( (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember ) ) )* )? otherlv_5= RightCurlyBracket ) )
            // InternalJsonParser.g:276:1: ( () otherlv_1= LeftCurlyBracket ( ( (lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember ) ) (otherlv_3= Comma ( (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember ) ) )* )? otherlv_5= RightCurlyBracket )
            {
            // InternalJsonParser.g:276:1: ( () otherlv_1= LeftCurlyBracket ( ( (lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember ) ) (otherlv_3= Comma ( (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember ) ) )* )? otherlv_5= RightCurlyBracket )
            // InternalJsonParser.g:276:2: () otherlv_1= LeftCurlyBracket ( ( (lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember ) ) (otherlv_3= Comma ( (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember ) ) )* )? otherlv_5= RightCurlyBracket
            {
            // InternalJsonParser.g:276:2: ()
            // InternalJsonParser.g:277:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getJsonAnnexObjectAccess().getJsonAnnexObjectAction_0(),
                        current);
                

            }

            otherlv_1=(Token)match(input,LeftCurlyBracket,FollowSets000.FOLLOW_3); 

                	newLeafNode(otherlv_1, grammarAccess.getJsonAnnexObjectAccess().getLeftCurlyBracketKeyword_1());
                
            // InternalJsonParser.g:287:1: ( ( (lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember ) ) (otherlv_3= Comma ( (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember ) ) )* )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==RULE_STRING) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // InternalJsonParser.g:287:2: ( (lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember ) ) (otherlv_3= Comma ( (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember ) ) )*
                    {
                    // InternalJsonParser.g:287:2: ( (lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember ) )
                    // InternalJsonParser.g:288:1: (lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember )
                    {
                    // InternalJsonParser.g:288:1: (lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember )
                    // InternalJsonParser.g:289:3: lv_jsonAnnexMembers_2_0= ruleJsonAnnexMember
                    {
                     
                    	        newCompositeNode(grammarAccess.getJsonAnnexObjectAccess().getJsonAnnexMembersJsonAnnexMemberParserRuleCall_2_0_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_4);
                    lv_jsonAnnexMembers_2_0=ruleJsonAnnexMember();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getJsonAnnexObjectRule());
                    	        }
                           		add(
                           			current, 
                           			"jsonAnnexMembers",
                            		lv_jsonAnnexMembers_2_0, 
                            		"com.collins.trustedsystems.briefcase.json.Json.JsonAnnexMember");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }

                    // InternalJsonParser.g:305:2: (otherlv_3= Comma ( (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember ) ) )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==Comma) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // InternalJsonParser.g:306:2: otherlv_3= Comma ( (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember ) )
                    	    {
                    	    otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_5); 

                    	        	newLeafNode(otherlv_3, grammarAccess.getJsonAnnexObjectAccess().getCommaKeyword_2_1_0());
                    	        
                    	    // InternalJsonParser.g:310:1: ( (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember ) )
                    	    // InternalJsonParser.g:311:1: (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember )
                    	    {
                    	    // InternalJsonParser.g:311:1: (lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember )
                    	    // InternalJsonParser.g:312:3: lv_jsonAnnexMembers_4_0= ruleJsonAnnexMember
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getJsonAnnexObjectAccess().getJsonAnnexMembersJsonAnnexMemberParserRuleCall_2_1_1_0()); 
                    	    	    
                    	    pushFollow(FollowSets000.FOLLOW_4);
                    	    lv_jsonAnnexMembers_4_0=ruleJsonAnnexMember();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getJsonAnnexObjectRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"jsonAnnexMembers",
                    	            		lv_jsonAnnexMembers_4_0, 
                    	            		"com.collins.trustedsystems.briefcase.json.Json.JsonAnnexMember");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_5=(Token)match(input,RightCurlyBracket,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_5, grammarAccess.getJsonAnnexObjectAccess().getRightCurlyBracketKeyword_3());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonAnnexObject"


    // $ANTLR start "entryRuleJsonAnnexArray"
    // InternalJsonParser.g:341:1: entryRuleJsonAnnexArray returns [EObject current=null] : iv_ruleJsonAnnexArray= ruleJsonAnnexArray EOF ;
    public final EObject entryRuleJsonAnnexArray() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleJsonAnnexArray = null;


        try {
            // InternalJsonParser.g:342:2: (iv_ruleJsonAnnexArray= ruleJsonAnnexArray EOF )
            // InternalJsonParser.g:343:2: iv_ruleJsonAnnexArray= ruleJsonAnnexArray EOF
            {
             newCompositeNode(grammarAccess.getJsonAnnexArrayRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonAnnexArray=ruleJsonAnnexArray();

            state._fsp--;

             current =iv_ruleJsonAnnexArray; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonAnnexArray"


    // $ANTLR start "ruleJsonAnnexArray"
    // InternalJsonParser.g:350:1: ruleJsonAnnexArray returns [EObject current=null] : ( () otherlv_1= LeftSquareBracket ( (lv_jsonAnnexElements_2_0= ruleJsonAnnexElement ) )? (otherlv_3= Comma ( (lv_jsonAnnexElements_4_0= ruleJsonAnnexElement ) ) )* otherlv_5= RightSquareBracket ) ;
    public final EObject ruleJsonAnnexArray() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_jsonAnnexElements_2_0 = null;

        EObject lv_jsonAnnexElements_4_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:353:28: ( ( () otherlv_1= LeftSquareBracket ( (lv_jsonAnnexElements_2_0= ruleJsonAnnexElement ) )? (otherlv_3= Comma ( (lv_jsonAnnexElements_4_0= ruleJsonAnnexElement ) ) )* otherlv_5= RightSquareBracket ) )
            // InternalJsonParser.g:354:1: ( () otherlv_1= LeftSquareBracket ( (lv_jsonAnnexElements_2_0= ruleJsonAnnexElement ) )? (otherlv_3= Comma ( (lv_jsonAnnexElements_4_0= ruleJsonAnnexElement ) ) )* otherlv_5= RightSquareBracket )
            {
            // InternalJsonParser.g:354:1: ( () otherlv_1= LeftSquareBracket ( (lv_jsonAnnexElements_2_0= ruleJsonAnnexElement ) )? (otherlv_3= Comma ( (lv_jsonAnnexElements_4_0= ruleJsonAnnexElement ) ) )* otherlv_5= RightSquareBracket )
            // InternalJsonParser.g:354:2: () otherlv_1= LeftSquareBracket ( (lv_jsonAnnexElements_2_0= ruleJsonAnnexElement ) )? (otherlv_3= Comma ( (lv_jsonAnnexElements_4_0= ruleJsonAnnexElement ) ) )* otherlv_5= RightSquareBracket
            {
            // InternalJsonParser.g:354:2: ()
            // InternalJsonParser.g:355:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getJsonAnnexArrayAccess().getJsonAnnexArrayAction_0(),
                        current);
                

            }

            otherlv_1=(Token)match(input,LeftSquareBracket,FollowSets000.FOLLOW_6); 

                	newLeafNode(otherlv_1, grammarAccess.getJsonAnnexArrayAccess().getLeftSquareBracketKeyword_1());
                
            // InternalJsonParser.g:365:1: ( (lv_jsonAnnexElements_2_0= ruleJsonAnnexElement ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==False||(LA6_0>=Null && LA6_0<=True)||LA6_0==LeftSquareBracket||LA6_0==LeftCurlyBracket||(LA6_0>=RULE_INT_NUMBER && LA6_0<=RULE_STRING)) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // InternalJsonParser.g:366:1: (lv_jsonAnnexElements_2_0= ruleJsonAnnexElement )
                    {
                    // InternalJsonParser.g:366:1: (lv_jsonAnnexElements_2_0= ruleJsonAnnexElement )
                    // InternalJsonParser.g:367:3: lv_jsonAnnexElements_2_0= ruleJsonAnnexElement
                    {
                     
                    	        newCompositeNode(grammarAccess.getJsonAnnexArrayAccess().getJsonAnnexElementsJsonAnnexElementParserRuleCall_2_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_7);
                    lv_jsonAnnexElements_2_0=ruleJsonAnnexElement();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getJsonAnnexArrayRule());
                    	        }
                           		add(
                           			current, 
                           			"jsonAnnexElements",
                            		lv_jsonAnnexElements_2_0, 
                            		"com.collins.trustedsystems.briefcase.json.Json.JsonAnnexElement");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }

            // InternalJsonParser.g:383:3: (otherlv_3= Comma ( (lv_jsonAnnexElements_4_0= ruleJsonAnnexElement ) ) )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==Comma) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // InternalJsonParser.g:384:2: otherlv_3= Comma ( (lv_jsonAnnexElements_4_0= ruleJsonAnnexElement ) )
            	    {
            	    otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_8); 

            	        	newLeafNode(otherlv_3, grammarAccess.getJsonAnnexArrayAccess().getCommaKeyword_3_0());
            	        
            	    // InternalJsonParser.g:388:1: ( (lv_jsonAnnexElements_4_0= ruleJsonAnnexElement ) )
            	    // InternalJsonParser.g:389:1: (lv_jsonAnnexElements_4_0= ruleJsonAnnexElement )
            	    {
            	    // InternalJsonParser.g:389:1: (lv_jsonAnnexElements_4_0= ruleJsonAnnexElement )
            	    // InternalJsonParser.g:390:3: lv_jsonAnnexElements_4_0= ruleJsonAnnexElement
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getJsonAnnexArrayAccess().getJsonAnnexElementsJsonAnnexElementParserRuleCall_3_1_0()); 
            	    	    
            	    pushFollow(FollowSets000.FOLLOW_7);
            	    lv_jsonAnnexElements_4_0=ruleJsonAnnexElement();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getJsonAnnexArrayRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"jsonAnnexElements",
            	            		lv_jsonAnnexElements_4_0, 
            	            		"com.collins.trustedsystems.briefcase.json.Json.JsonAnnexElement");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            otherlv_5=(Token)match(input,RightSquareBracket,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_5, grammarAccess.getJsonAnnexArrayAccess().getRightSquareBracketKeyword_4());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonAnnexArray"


    // $ANTLR start "entryRuleJsonAnnexMember"
    // InternalJsonParser.g:419:1: entryRuleJsonAnnexMember returns [EObject current=null] : iv_ruleJsonAnnexMember= ruleJsonAnnexMember EOF ;
    public final EObject entryRuleJsonAnnexMember() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleJsonAnnexMember = null;


        try {
            // InternalJsonParser.g:420:2: (iv_ruleJsonAnnexMember= ruleJsonAnnexMember EOF )
            // InternalJsonParser.g:421:2: iv_ruleJsonAnnexMember= ruleJsonAnnexMember EOF
            {
             newCompositeNode(grammarAccess.getJsonAnnexMemberRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonAnnexMember=ruleJsonAnnexMember();

            state._fsp--;

             current =iv_ruleJsonAnnexMember; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonAnnexMember"


    // $ANTLR start "ruleJsonAnnexMember"
    // InternalJsonParser.g:428:1: ruleJsonAnnexMember returns [EObject current=null] : ( ( (lv_key_0_0= ruleJsonAnnexString ) ) otherlv_1= Colon ( (lv_jsonAnnexElement_2_0= ruleJsonAnnexElement ) ) ) ;
    public final EObject ruleJsonAnnexMember() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        EObject lv_key_0_0 = null;

        EObject lv_jsonAnnexElement_2_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:431:28: ( ( ( (lv_key_0_0= ruleJsonAnnexString ) ) otherlv_1= Colon ( (lv_jsonAnnexElement_2_0= ruleJsonAnnexElement ) ) ) )
            // InternalJsonParser.g:432:1: ( ( (lv_key_0_0= ruleJsonAnnexString ) ) otherlv_1= Colon ( (lv_jsonAnnexElement_2_0= ruleJsonAnnexElement ) ) )
            {
            // InternalJsonParser.g:432:1: ( ( (lv_key_0_0= ruleJsonAnnexString ) ) otherlv_1= Colon ( (lv_jsonAnnexElement_2_0= ruleJsonAnnexElement ) ) )
            // InternalJsonParser.g:432:2: ( (lv_key_0_0= ruleJsonAnnexString ) ) otherlv_1= Colon ( (lv_jsonAnnexElement_2_0= ruleJsonAnnexElement ) )
            {
            // InternalJsonParser.g:432:2: ( (lv_key_0_0= ruleJsonAnnexString ) )
            // InternalJsonParser.g:433:1: (lv_key_0_0= ruleJsonAnnexString )
            {
            // InternalJsonParser.g:433:1: (lv_key_0_0= ruleJsonAnnexString )
            // InternalJsonParser.g:434:3: lv_key_0_0= ruleJsonAnnexString
            {
             
            	        newCompositeNode(grammarAccess.getJsonAnnexMemberAccess().getKeyJsonAnnexStringParserRuleCall_0_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_9);
            lv_key_0_0=ruleJsonAnnexString();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getJsonAnnexMemberRule());
            	        }
                   		set(
                   			current, 
                   			"key",
                    		lv_key_0_0, 
                    		"com.collins.trustedsystems.briefcase.json.Json.JsonAnnexString");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,Colon,FollowSets000.FOLLOW_8); 

                	newLeafNode(otherlv_1, grammarAccess.getJsonAnnexMemberAccess().getColonKeyword_1());
                
            // InternalJsonParser.g:455:1: ( (lv_jsonAnnexElement_2_0= ruleJsonAnnexElement ) )
            // InternalJsonParser.g:456:1: (lv_jsonAnnexElement_2_0= ruleJsonAnnexElement )
            {
            // InternalJsonParser.g:456:1: (lv_jsonAnnexElement_2_0= ruleJsonAnnexElement )
            // InternalJsonParser.g:457:3: lv_jsonAnnexElement_2_0= ruleJsonAnnexElement
            {
             
            	        newCompositeNode(grammarAccess.getJsonAnnexMemberAccess().getJsonAnnexElementJsonAnnexElementParserRuleCall_2_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_2);
            lv_jsonAnnexElement_2_0=ruleJsonAnnexElement();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getJsonAnnexMemberRule());
            	        }
                   		set(
                   			current, 
                   			"jsonAnnexElement",
                    		lv_jsonAnnexElement_2_0, 
                    		"com.collins.trustedsystems.briefcase.json.Json.JsonAnnexElement");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonAnnexMember"


    // $ANTLR start "entryRuleJsonAnnexString"
    // InternalJsonParser.g:481:1: entryRuleJsonAnnexString returns [EObject current=null] : iv_ruleJsonAnnexString= ruleJsonAnnexString EOF ;
    public final EObject entryRuleJsonAnnexString() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleJsonAnnexString = null;


        try {
            // InternalJsonParser.g:482:2: (iv_ruleJsonAnnexString= ruleJsonAnnexString EOF )
            // InternalJsonParser.g:483:2: iv_ruleJsonAnnexString= ruleJsonAnnexString EOF
            {
             newCompositeNode(grammarAccess.getJsonAnnexStringRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonAnnexString=ruleJsonAnnexString();

            state._fsp--;

             current =iv_ruleJsonAnnexString; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonAnnexString"


    // $ANTLR start "ruleJsonAnnexString"
    // InternalJsonParser.g:490:1: ruleJsonAnnexString returns [EObject current=null] : ( () ( (lv_value_1_0= ruleJsonString ) ) ) ;
    public final EObject ruleJsonAnnexString() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_value_1_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:493:28: ( ( () ( (lv_value_1_0= ruleJsonString ) ) ) )
            // InternalJsonParser.g:494:1: ( () ( (lv_value_1_0= ruleJsonString ) ) )
            {
            // InternalJsonParser.g:494:1: ( () ( (lv_value_1_0= ruleJsonString ) ) )
            // InternalJsonParser.g:494:2: () ( (lv_value_1_0= ruleJsonString ) )
            {
            // InternalJsonParser.g:494:2: ()
            // InternalJsonParser.g:495:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getJsonAnnexStringAccess().getJsonAnnexStringAction_0(),
                        current);
                

            }

            // InternalJsonParser.g:500:2: ( (lv_value_1_0= ruleJsonString ) )
            // InternalJsonParser.g:501:1: (lv_value_1_0= ruleJsonString )
            {
            // InternalJsonParser.g:501:1: (lv_value_1_0= ruleJsonString )
            // InternalJsonParser.g:502:3: lv_value_1_0= ruleJsonString
            {
             
            	        newCompositeNode(grammarAccess.getJsonAnnexStringAccess().getValueJsonStringParserRuleCall_1_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_1_0=ruleJsonString();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getJsonAnnexStringRule());
            	        }
                   		set(
                   			current, 
                   			"value",
                    		lv_value_1_0, 
                    		"com.collins.trustedsystems.briefcase.json.Json.JsonString");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonAnnexString"


    // $ANTLR start "entryRuleJsonAnnexNumber"
    // InternalJsonParser.g:526:1: entryRuleJsonAnnexNumber returns [EObject current=null] : iv_ruleJsonAnnexNumber= ruleJsonAnnexNumber EOF ;
    public final EObject entryRuleJsonAnnexNumber() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleJsonAnnexNumber = null;


        try {
            // InternalJsonParser.g:527:2: (iv_ruleJsonAnnexNumber= ruleJsonAnnexNumber EOF )
            // InternalJsonParser.g:528:2: iv_ruleJsonAnnexNumber= ruleJsonAnnexNumber EOF
            {
             newCompositeNode(grammarAccess.getJsonAnnexNumberRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonAnnexNumber=ruleJsonAnnexNumber();

            state._fsp--;

             current =iv_ruleJsonAnnexNumber; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonAnnexNumber"


    // $ANTLR start "ruleJsonAnnexNumber"
    // InternalJsonParser.g:535:1: ruleJsonAnnexNumber returns [EObject current=null] : ( ( () ( (lv_value_1_0= ruleSignedInteger ) ) ) | ( () ( (lv_value_3_0= ruleSignedReal ) ) ) ) ;
    public final EObject ruleJsonAnnexNumber() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_value_1_0 = null;

        AntlrDatatypeRuleToken lv_value_3_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:538:28: ( ( ( () ( (lv_value_1_0= ruleSignedInteger ) ) ) | ( () ( (lv_value_3_0= ruleSignedReal ) ) ) ) )
            // InternalJsonParser.g:539:1: ( ( () ( (lv_value_1_0= ruleSignedInteger ) ) ) | ( () ( (lv_value_3_0= ruleSignedReal ) ) ) )
            {
            // InternalJsonParser.g:539:1: ( ( () ( (lv_value_1_0= ruleSignedInteger ) ) ) | ( () ( (lv_value_3_0= ruleSignedReal ) ) ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_INT_NUMBER) ) {
                alt8=1;
            }
            else if ( (LA8_0==RULE_REAL_NUMBER) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // InternalJsonParser.g:539:2: ( () ( (lv_value_1_0= ruleSignedInteger ) ) )
                    {
                    // InternalJsonParser.g:539:2: ( () ( (lv_value_1_0= ruleSignedInteger ) ) )
                    // InternalJsonParser.g:539:3: () ( (lv_value_1_0= ruleSignedInteger ) )
                    {
                    // InternalJsonParser.g:539:3: ()
                    // InternalJsonParser.g:540:5: 
                    {

                            current = forceCreateModelElement(
                                grammarAccess.getJsonAnnexNumberAccess().getJsonAnnexIntegerAction_0_0(),
                                current);
                        

                    }

                    // InternalJsonParser.g:545:2: ( (lv_value_1_0= ruleSignedInteger ) )
                    // InternalJsonParser.g:546:1: (lv_value_1_0= ruleSignedInteger )
                    {
                    // InternalJsonParser.g:546:1: (lv_value_1_0= ruleSignedInteger )
                    // InternalJsonParser.g:547:3: lv_value_1_0= ruleSignedInteger
                    {
                     
                    	        newCompositeNode(grammarAccess.getJsonAnnexNumberAccess().getValueSignedIntegerParserRuleCall_0_1_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_value_1_0=ruleSignedInteger();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getJsonAnnexNumberRule());
                    	        }
                           		set(
                           			current, 
                           			"value",
                            		lv_value_1_0, 
                            		"com.collins.trustedsystems.briefcase.json.Json.SignedInteger");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalJsonParser.g:564:6: ( () ( (lv_value_3_0= ruleSignedReal ) ) )
                    {
                    // InternalJsonParser.g:564:6: ( () ( (lv_value_3_0= ruleSignedReal ) ) )
                    // InternalJsonParser.g:564:7: () ( (lv_value_3_0= ruleSignedReal ) )
                    {
                    // InternalJsonParser.g:564:7: ()
                    // InternalJsonParser.g:565:5: 
                    {

                            current = forceCreateModelElement(
                                grammarAccess.getJsonAnnexNumberAccess().getJsonAnnexRealAction_1_0(),
                                current);
                        

                    }

                    // InternalJsonParser.g:570:2: ( (lv_value_3_0= ruleSignedReal ) )
                    // InternalJsonParser.g:571:1: (lv_value_3_0= ruleSignedReal )
                    {
                    // InternalJsonParser.g:571:1: (lv_value_3_0= ruleSignedReal )
                    // InternalJsonParser.g:572:3: lv_value_3_0= ruleSignedReal
                    {
                     
                    	        newCompositeNode(grammarAccess.getJsonAnnexNumberAccess().getValueSignedRealParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_value_3_0=ruleSignedReal();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getJsonAnnexNumberRule());
                    	        }
                           		set(
                           			current, 
                           			"value",
                            		lv_value_3_0, 
                            		"com.collins.trustedsystems.briefcase.json.Json.SignedReal");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonAnnexNumber"


    // $ANTLR start "entryRuleSignedInteger"
    // InternalJsonParser.g:596:1: entryRuleSignedInteger returns [String current=null] : iv_ruleSignedInteger= ruleSignedInteger EOF ;
    public final String entryRuleSignedInteger() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSignedInteger = null;


        try {
            // InternalJsonParser.g:597:1: (iv_ruleSignedInteger= ruleSignedInteger EOF )
            // InternalJsonParser.g:598:2: iv_ruleSignedInteger= ruleSignedInteger EOF
            {
             newCompositeNode(grammarAccess.getSignedIntegerRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSignedInteger=ruleSignedInteger();

            state._fsp--;

             current =iv_ruleSignedInteger.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSignedInteger"


    // $ANTLR start "ruleSignedInteger"
    // InternalJsonParser.g:605:1: ruleSignedInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INT_NUMBER_0= RULE_INT_NUMBER ;
    public final AntlrDatatypeRuleToken ruleSignedInteger() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INT_NUMBER_0=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:609:6: (this_INT_NUMBER_0= RULE_INT_NUMBER )
            // InternalJsonParser.g:610:5: this_INT_NUMBER_0= RULE_INT_NUMBER
            {
            this_INT_NUMBER_0=(Token)match(input,RULE_INT_NUMBER,FollowSets000.FOLLOW_2); 

            		current.merge(this_INT_NUMBER_0);
                
             
                newLeafNode(this_INT_NUMBER_0, grammarAccess.getSignedIntegerAccess().getINT_NUMBERTerminalRuleCall()); 
                

            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSignedInteger"


    // $ANTLR start "entryRuleSignedReal"
    // InternalJsonParser.g:625:1: entryRuleSignedReal returns [String current=null] : iv_ruleSignedReal= ruleSignedReal EOF ;
    public final String entryRuleSignedReal() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSignedReal = null;


        try {
            // InternalJsonParser.g:626:1: (iv_ruleSignedReal= ruleSignedReal EOF )
            // InternalJsonParser.g:627:2: iv_ruleSignedReal= ruleSignedReal EOF
            {
             newCompositeNode(grammarAccess.getSignedRealRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSignedReal=ruleSignedReal();

            state._fsp--;

             current =iv_ruleSignedReal.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSignedReal"


    // $ANTLR start "ruleSignedReal"
    // InternalJsonParser.g:634:1: ruleSignedReal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_REAL_NUMBER_0= RULE_REAL_NUMBER ;
    public final AntlrDatatypeRuleToken ruleSignedReal() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_REAL_NUMBER_0=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:638:6: (this_REAL_NUMBER_0= RULE_REAL_NUMBER )
            // InternalJsonParser.g:639:5: this_REAL_NUMBER_0= RULE_REAL_NUMBER
            {
            this_REAL_NUMBER_0=(Token)match(input,RULE_REAL_NUMBER,FollowSets000.FOLLOW_2); 

            		current.merge(this_REAL_NUMBER_0);
                
             
                newLeafNode(this_REAL_NUMBER_0, grammarAccess.getSignedRealAccess().getREAL_NUMBERTerminalRuleCall()); 
                

            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSignedReal"


    // $ANTLR start "entryRuleJsonAnnexBoolean"
    // InternalJsonParser.g:654:1: entryRuleJsonAnnexBoolean returns [EObject current=null] : iv_ruleJsonAnnexBoolean= ruleJsonAnnexBoolean EOF ;
    public final EObject entryRuleJsonAnnexBoolean() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleJsonAnnexBoolean = null;


        try {
            // InternalJsonParser.g:655:2: (iv_ruleJsonAnnexBoolean= ruleJsonAnnexBoolean EOF )
            // InternalJsonParser.g:656:2: iv_ruleJsonAnnexBoolean= ruleJsonAnnexBoolean EOF
            {
             newCompositeNode(grammarAccess.getJsonAnnexBooleanRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonAnnexBoolean=ruleJsonAnnexBoolean();

            state._fsp--;

             current =iv_ruleJsonAnnexBoolean; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonAnnexBoolean"


    // $ANTLR start "ruleJsonAnnexBoolean"
    // InternalJsonParser.g:663:1: ruleJsonAnnexBoolean returns [EObject current=null] : ( ( () otherlv_1= True ) | ( () otherlv_3= False ) ) ;
    public final EObject ruleJsonAnnexBoolean() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:666:28: ( ( ( () otherlv_1= True ) | ( () otherlv_3= False ) ) )
            // InternalJsonParser.g:667:1: ( ( () otherlv_1= True ) | ( () otherlv_3= False ) )
            {
            // InternalJsonParser.g:667:1: ( ( () otherlv_1= True ) | ( () otherlv_3= False ) )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==True) ) {
                alt9=1;
            }
            else if ( (LA9_0==False) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // InternalJsonParser.g:667:2: ( () otherlv_1= True )
                    {
                    // InternalJsonParser.g:667:2: ( () otherlv_1= True )
                    // InternalJsonParser.g:667:3: () otherlv_1= True
                    {
                    // InternalJsonParser.g:667:3: ()
                    // InternalJsonParser.g:668:5: 
                    {

                            current = forceCreateModelElement(
                                grammarAccess.getJsonAnnexBooleanAccess().getJsonAnnexTrueAction_0_0(),
                                current);
                        

                    }

                    otherlv_1=(Token)match(input,True,FollowSets000.FOLLOW_2); 

                        	newLeafNode(otherlv_1, grammarAccess.getJsonAnnexBooleanAccess().getTrueKeyword_0_1());
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalJsonParser.g:679:6: ( () otherlv_3= False )
                    {
                    // InternalJsonParser.g:679:6: ( () otherlv_3= False )
                    // InternalJsonParser.g:679:7: () otherlv_3= False
                    {
                    // InternalJsonParser.g:679:7: ()
                    // InternalJsonParser.g:680:5: 
                    {

                            current = forceCreateModelElement(
                                grammarAccess.getJsonAnnexBooleanAccess().getJsonAnnexFalseAction_1_0(),
                                current);
                        

                    }

                    otherlv_3=(Token)match(input,False,FollowSets000.FOLLOW_2); 

                        	newLeafNode(otherlv_3, grammarAccess.getJsonAnnexBooleanAccess().getFalseKeyword_1_1());
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonAnnexBoolean"


    // $ANTLR start "entryRuleJsonAnnexNull"
    // InternalJsonParser.g:698:1: entryRuleJsonAnnexNull returns [EObject current=null] : iv_ruleJsonAnnexNull= ruleJsonAnnexNull EOF ;
    public final EObject entryRuleJsonAnnexNull() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleJsonAnnexNull = null;


        try {
            // InternalJsonParser.g:699:2: (iv_ruleJsonAnnexNull= ruleJsonAnnexNull EOF )
            // InternalJsonParser.g:700:2: iv_ruleJsonAnnexNull= ruleJsonAnnexNull EOF
            {
             newCompositeNode(grammarAccess.getJsonAnnexNullRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonAnnexNull=ruleJsonAnnexNull();

            state._fsp--;

             current =iv_ruleJsonAnnexNull; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonAnnexNull"


    // $ANTLR start "ruleJsonAnnexNull"
    // InternalJsonParser.g:707:1: ruleJsonAnnexNull returns [EObject current=null] : ( () otherlv_1= Null ) ;
    public final EObject ruleJsonAnnexNull() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:710:28: ( ( () otherlv_1= Null ) )
            // InternalJsonParser.g:711:1: ( () otherlv_1= Null )
            {
            // InternalJsonParser.g:711:1: ( () otherlv_1= Null )
            // InternalJsonParser.g:711:2: () otherlv_1= Null
            {
            // InternalJsonParser.g:711:2: ()
            // InternalJsonParser.g:712:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getJsonAnnexNullAccess().getJsonAnnexNullAction_0(),
                        current);
                

            }

            otherlv_1=(Token)match(input,Null,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_1, grammarAccess.getJsonAnnexNullAccess().getNullKeyword_1());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonAnnexNull"


    // $ANTLR start "entryRuleJsonString"
    // InternalJsonParser.g:730:1: entryRuleJsonString returns [String current=null] : iv_ruleJsonString= ruleJsonString EOF ;
    public final String entryRuleJsonString() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleJsonString = null;


        try {
            // InternalJsonParser.g:731:1: (iv_ruleJsonString= ruleJsonString EOF )
            // InternalJsonParser.g:732:2: iv_ruleJsonString= ruleJsonString EOF
            {
             newCompositeNode(grammarAccess.getJsonStringRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleJsonString=ruleJsonString();

            state._fsp--;

             current =iv_ruleJsonString.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleJsonString"


    // $ANTLR start "ruleJsonString"
    // InternalJsonParser.g:739:1: ruleJsonString returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleJsonString() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:743:6: (this_STRING_0= RULE_STRING )
            // InternalJsonParser.g:744:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FollowSets000.FOLLOW_2); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getJsonStringAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleJsonString"


    // $ANTLR start "entryRuleContainedPropertyAssociation"
    // InternalJsonParser.g:761:1: entryRuleContainedPropertyAssociation returns [EObject current=null] : iv_ruleContainedPropertyAssociation= ruleContainedPropertyAssociation EOF ;
    public final EObject entryRuleContainedPropertyAssociation() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleContainedPropertyAssociation = null;


        try {
            // InternalJsonParser.g:762:2: (iv_ruleContainedPropertyAssociation= ruleContainedPropertyAssociation EOF )
            // InternalJsonParser.g:763:2: iv_ruleContainedPropertyAssociation= ruleContainedPropertyAssociation EOF
            {
             newCompositeNode(grammarAccess.getContainedPropertyAssociationRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleContainedPropertyAssociation=ruleContainedPropertyAssociation();

            state._fsp--;

             current =iv_ruleContainedPropertyAssociation; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleContainedPropertyAssociation"


    // $ANTLR start "ruleContainedPropertyAssociation"
    // InternalJsonParser.g:770:1: ruleContainedPropertyAssociation returns [EObject current=null] : ( ( ( ruleQPREF ) ) (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) ) ( (lv_constant_3_0= Constant ) )? ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* ) ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )? ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )? otherlv_15= Semicolon ) ;
    public final EObject ruleContainedPropertyAssociation() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token lv_append_2_0=null;
        Token lv_constant_3_0=null;
        Token otherlv_5=null;
        Token otherlv_9=null;
        Token otherlv_12=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        EObject lv_ownedValue_4_0 = null;

        EObject lv_ownedValue_6_0 = null;

        EObject lv_appliesTo_8_0 = null;

        EObject lv_appliesTo_10_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:773:28: ( ( ( ( ruleQPREF ) ) (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) ) ( (lv_constant_3_0= Constant ) )? ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* ) ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )? ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )? otherlv_15= Semicolon ) )
            // InternalJsonParser.g:774:1: ( ( ( ruleQPREF ) ) (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) ) ( (lv_constant_3_0= Constant ) )? ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* ) ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )? ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )? otherlv_15= Semicolon )
            {
            // InternalJsonParser.g:774:1: ( ( ( ruleQPREF ) ) (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) ) ( (lv_constant_3_0= Constant ) )? ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* ) ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )? ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )? otherlv_15= Semicolon )
            // InternalJsonParser.g:774:2: ( ( ruleQPREF ) ) (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) ) ( (lv_constant_3_0= Constant ) )? ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* ) ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )? ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )? otherlv_15= Semicolon
            {
            // InternalJsonParser.g:774:2: ( ( ruleQPREF ) )
            // InternalJsonParser.g:775:1: ( ruleQPREF )
            {
            // InternalJsonParser.g:775:1: ( ruleQPREF )
            // InternalJsonParser.g:776:3: ruleQPREF
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getContainedPropertyAssociationRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getPropertyPropertyCrossReference_0_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_10);
            ruleQPREF();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalJsonParser.g:790:2: (otherlv_1= EqualsSignGreaterThanSign | ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==EqualsSignGreaterThanSign) ) {
                alt10=1;
            }
            else if ( (LA10_0==PlusSignEqualsSignGreaterThanSign) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // InternalJsonParser.g:791:2: otherlv_1= EqualsSignGreaterThanSign
                    {
                    otherlv_1=(Token)match(input,EqualsSignGreaterThanSign,FollowSets000.FOLLOW_11); 

                        	newLeafNode(otherlv_1, grammarAccess.getContainedPropertyAssociationAccess().getEqualsSignGreaterThanSignKeyword_1_0());
                        

                    }
                    break;
                case 2 :
                    // InternalJsonParser.g:796:6: ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) )
                    {
                    // InternalJsonParser.g:796:6: ( (lv_append_2_0= PlusSignEqualsSignGreaterThanSign ) )
                    // InternalJsonParser.g:797:1: (lv_append_2_0= PlusSignEqualsSignGreaterThanSign )
                    {
                    // InternalJsonParser.g:797:1: (lv_append_2_0= PlusSignEqualsSignGreaterThanSign )
                    // InternalJsonParser.g:798:3: lv_append_2_0= PlusSignEqualsSignGreaterThanSign
                    {
                    lv_append_2_0=(Token)match(input,PlusSignEqualsSignGreaterThanSign,FollowSets000.FOLLOW_11); 

                            newLeafNode(lv_append_2_0, grammarAccess.getContainedPropertyAssociationAccess().getAppendPlusSignEqualsSignGreaterThanSignKeyword_1_1_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getContainedPropertyAssociationRule());
                    	        }
                           		setWithLastConsumed(current, "append", true, "+=>");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalJsonParser.g:812:3: ( (lv_constant_3_0= Constant ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==Constant) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalJsonParser.g:813:1: (lv_constant_3_0= Constant )
                    {
                    // InternalJsonParser.g:813:1: (lv_constant_3_0= Constant )
                    // InternalJsonParser.g:814:3: lv_constant_3_0= Constant
                    {
                    lv_constant_3_0=(Token)match(input,Constant,FollowSets000.FOLLOW_11); 

                            newLeafNode(lv_constant_3_0, grammarAccess.getContainedPropertyAssociationAccess().getConstantConstantKeyword_2_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getContainedPropertyAssociationRule());
                    	        }
                           		setWithLastConsumed(current, "constant", true, "constant");
                    	    

                    }


                    }
                    break;

            }

            // InternalJsonParser.g:828:3: ( ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )* )
            // InternalJsonParser.g:828:4: ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) ) (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )*
            {
            // InternalJsonParser.g:828:4: ( (lv_ownedValue_4_0= ruleOptionalModalPropertyValue ) )
            // InternalJsonParser.g:829:1: (lv_ownedValue_4_0= ruleOptionalModalPropertyValue )
            {
            // InternalJsonParser.g:829:1: (lv_ownedValue_4_0= ruleOptionalModalPropertyValue )
            // InternalJsonParser.g:830:3: lv_ownedValue_4_0= ruleOptionalModalPropertyValue
            {
             
            	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getOwnedValueOptionalModalPropertyValueParserRuleCall_3_0_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_12);
            lv_ownedValue_4_0=ruleOptionalModalPropertyValue();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getContainedPropertyAssociationRule());
            	        }
                   		add(
                   			current, 
                   			"ownedValue",
                    		lv_ownedValue_4_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.OptionalModalPropertyValue");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalJsonParser.g:846:2: (otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) ) )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==Comma) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // InternalJsonParser.g:847:2: otherlv_5= Comma ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) )
            	    {
            	    otherlv_5=(Token)match(input,Comma,FollowSets000.FOLLOW_11); 

            	        	newLeafNode(otherlv_5, grammarAccess.getContainedPropertyAssociationAccess().getCommaKeyword_3_1_0());
            	        
            	    // InternalJsonParser.g:851:1: ( (lv_ownedValue_6_0= ruleOptionalModalPropertyValue ) )
            	    // InternalJsonParser.g:852:1: (lv_ownedValue_6_0= ruleOptionalModalPropertyValue )
            	    {
            	    // InternalJsonParser.g:852:1: (lv_ownedValue_6_0= ruleOptionalModalPropertyValue )
            	    // InternalJsonParser.g:853:3: lv_ownedValue_6_0= ruleOptionalModalPropertyValue
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getOwnedValueOptionalModalPropertyValueParserRuleCall_3_1_1_0()); 
            	    	    
            	    pushFollow(FollowSets000.FOLLOW_12);
            	    lv_ownedValue_6_0=ruleOptionalModalPropertyValue();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getContainedPropertyAssociationRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"ownedValue",
            	            		lv_ownedValue_6_0, 
            	            		"org.osate.xtext.aadl2.properties.Properties.OptionalModalPropertyValue");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

            // InternalJsonParser.g:869:5: ( ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )* )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==Applies) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // InternalJsonParser.g:870:5: ruleAppliesToKeywords ( (lv_appliesTo_8_0= ruleContainmentPath ) ) (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )*
                    {
                     
                            newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getAppliesToKeywordsParserRuleCall_4_0()); 
                        
                    pushFollow(FollowSets000.FOLLOW_13);
                    ruleAppliesToKeywords();

                    state._fsp--;


                            afterParserOrEnumRuleCall();
                        
                    // InternalJsonParser.g:877:1: ( (lv_appliesTo_8_0= ruleContainmentPath ) )
                    // InternalJsonParser.g:878:1: (lv_appliesTo_8_0= ruleContainmentPath )
                    {
                    // InternalJsonParser.g:878:1: (lv_appliesTo_8_0= ruleContainmentPath )
                    // InternalJsonParser.g:879:3: lv_appliesTo_8_0= ruleContainmentPath
                    {
                     
                    	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getAppliesToContainmentPathParserRuleCall_4_1_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_14);
                    lv_appliesTo_8_0=ruleContainmentPath();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getContainedPropertyAssociationRule());
                    	        }
                           		add(
                           			current, 
                           			"appliesTo",
                            		lv_appliesTo_8_0, 
                            		"org.osate.xtext.aadl2.properties.Properties.ContainmentPath");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }

                    // InternalJsonParser.g:895:2: (otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) ) )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==Comma) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // InternalJsonParser.g:896:2: otherlv_9= Comma ( (lv_appliesTo_10_0= ruleContainmentPath ) )
                    	    {
                    	    otherlv_9=(Token)match(input,Comma,FollowSets000.FOLLOW_13); 

                    	        	newLeafNode(otherlv_9, grammarAccess.getContainedPropertyAssociationAccess().getCommaKeyword_4_2_0());
                    	        
                    	    // InternalJsonParser.g:900:1: ( (lv_appliesTo_10_0= ruleContainmentPath ) )
                    	    // InternalJsonParser.g:901:1: (lv_appliesTo_10_0= ruleContainmentPath )
                    	    {
                    	    // InternalJsonParser.g:901:1: (lv_appliesTo_10_0= ruleContainmentPath )
                    	    // InternalJsonParser.g:902:3: lv_appliesTo_10_0= ruleContainmentPath
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getAppliesToContainmentPathParserRuleCall_4_2_1_0()); 
                    	    	    
                    	    pushFollow(FollowSets000.FOLLOW_14);
                    	    lv_appliesTo_10_0=ruleContainmentPath();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getContainedPropertyAssociationRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"appliesTo",
                    	            		lv_appliesTo_10_0, 
                    	            		"org.osate.xtext.aadl2.properties.Properties.ContainmentPath");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);


                    }
                    break;

            }

            // InternalJsonParser.g:918:6: ( ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==In) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // InternalJsonParser.g:919:5: ruleInBindingKeywords otherlv_12= LeftParenthesis ( ( ruleQCREF ) ) otherlv_14= RightParenthesis
                    {
                     
                            newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getInBindingKeywordsParserRuleCall_5_0()); 
                        
                    pushFollow(FollowSets000.FOLLOW_15);
                    ruleInBindingKeywords();

                    state._fsp--;


                            afterParserOrEnumRuleCall();
                        
                    otherlv_12=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_13); 

                        	newLeafNode(otherlv_12, grammarAccess.getContainedPropertyAssociationAccess().getLeftParenthesisKeyword_5_1());
                        
                    // InternalJsonParser.g:931:1: ( ( ruleQCREF ) )
                    // InternalJsonParser.g:932:1: ( ruleQCREF )
                    {
                    // InternalJsonParser.g:932:1: ( ruleQCREF )
                    // InternalJsonParser.g:933:3: ruleQCREF
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getContainedPropertyAssociationRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getContainedPropertyAssociationAccess().getInBindingClassifierCrossReference_5_2_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_16);
                    ruleQCREF();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }

                    otherlv_14=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_17); 

                        	newLeafNode(otherlv_14, grammarAccess.getContainedPropertyAssociationAccess().getRightParenthesisKeyword_5_3());
                        

                    }
                    break;

            }

            otherlv_15=(Token)match(input,Semicolon,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_15, grammarAccess.getContainedPropertyAssociationAccess().getSemicolonKeyword_6());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleContainedPropertyAssociation"


    // $ANTLR start "entryRuleContainmentPath"
    // InternalJsonParser.g:969:1: entryRuleContainmentPath returns [EObject current=null] : iv_ruleContainmentPath= ruleContainmentPath EOF ;
    public final EObject entryRuleContainmentPath() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleContainmentPath = null;


        try {
            // InternalJsonParser.g:970:2: (iv_ruleContainmentPath= ruleContainmentPath EOF )
            // InternalJsonParser.g:971:2: iv_ruleContainmentPath= ruleContainmentPath EOF
            {
             newCompositeNode(grammarAccess.getContainmentPathRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleContainmentPath=ruleContainmentPath();

            state._fsp--;

             current =iv_ruleContainmentPath; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleContainmentPath"


    // $ANTLR start "ruleContainmentPath"
    // InternalJsonParser.g:978:1: ruleContainmentPath returns [EObject current=null] : ( (lv_path_0_0= ruleContainmentPathElement ) ) ;
    public final EObject ruleContainmentPath() throws RecognitionException {
        EObject current = null;

        EObject lv_path_0_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:981:28: ( ( (lv_path_0_0= ruleContainmentPathElement ) ) )
            // InternalJsonParser.g:982:1: ( (lv_path_0_0= ruleContainmentPathElement ) )
            {
            // InternalJsonParser.g:982:1: ( (lv_path_0_0= ruleContainmentPathElement ) )
            // InternalJsonParser.g:983:1: (lv_path_0_0= ruleContainmentPathElement )
            {
            // InternalJsonParser.g:983:1: (lv_path_0_0= ruleContainmentPathElement )
            // InternalJsonParser.g:984:3: lv_path_0_0= ruleContainmentPathElement
            {
             
            	        newCompositeNode(grammarAccess.getContainmentPathAccess().getPathContainmentPathElementParserRuleCall_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_2);
            lv_path_0_0=ruleContainmentPathElement();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getContainmentPathRule());
            	        }
                   		set(
                   			current, 
                   			"path",
                    		lv_path_0_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.ContainmentPathElement");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleContainmentPath"


    // $ANTLR start "entryRuleOptionalModalPropertyValue"
    // InternalJsonParser.g:1010:1: entryRuleOptionalModalPropertyValue returns [EObject current=null] : iv_ruleOptionalModalPropertyValue= ruleOptionalModalPropertyValue EOF ;
    public final EObject entryRuleOptionalModalPropertyValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOptionalModalPropertyValue = null;


        try {
            // InternalJsonParser.g:1011:2: (iv_ruleOptionalModalPropertyValue= ruleOptionalModalPropertyValue EOF )
            // InternalJsonParser.g:1012:2: iv_ruleOptionalModalPropertyValue= ruleOptionalModalPropertyValue EOF
            {
             newCompositeNode(grammarAccess.getOptionalModalPropertyValueRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleOptionalModalPropertyValue=ruleOptionalModalPropertyValue();

            state._fsp--;

             current =iv_ruleOptionalModalPropertyValue; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOptionalModalPropertyValue"


    // $ANTLR start "ruleOptionalModalPropertyValue"
    // InternalJsonParser.g:1019:1: ruleOptionalModalPropertyValue returns [EObject current=null] : ( ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )? ) ;
    public final EObject ruleOptionalModalPropertyValue() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        EObject lv_ownedValue_0_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:1022:28: ( ( ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )? ) )
            // InternalJsonParser.g:1023:1: ( ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )? )
            {
            // InternalJsonParser.g:1023:1: ( ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )? )
            // InternalJsonParser.g:1023:2: ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )?
            {
            // InternalJsonParser.g:1023:2: ( (lv_ownedValue_0_0= rulePropertyExpression ) )
            // InternalJsonParser.g:1024:1: (lv_ownedValue_0_0= rulePropertyExpression )
            {
            // InternalJsonParser.g:1024:1: (lv_ownedValue_0_0= rulePropertyExpression )
            // InternalJsonParser.g:1025:3: lv_ownedValue_0_0= rulePropertyExpression
            {
             
            	        newCompositeNode(grammarAccess.getOptionalModalPropertyValueAccess().getOwnedValuePropertyExpressionParserRuleCall_0_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_18);
            lv_ownedValue_0_0=rulePropertyExpression();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getOptionalModalPropertyValueRule());
            	        }
                   		set(
                   			current, 
                   			"ownedValue",
                    		lv_ownedValue_0_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.PropertyExpression");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalJsonParser.g:1041:2: ( ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==In) ) {
                int LA17_1 = input.LA(2);

                if ( (LA17_1==Modes) ) {
                    alt17=1;
                }
            }
            switch (alt17) {
                case 1 :
                    // InternalJsonParser.g:1042:5: ruleInModesKeywords otherlv_2= LeftParenthesis ( (otherlv_3= RULE_ID ) ) (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )* otherlv_6= RightParenthesis
                    {
                     
                            newCompositeNode(grammarAccess.getOptionalModalPropertyValueAccess().getInModesKeywordsParserRuleCall_1_0()); 
                        
                    pushFollow(FollowSets000.FOLLOW_15);
                    ruleInModesKeywords();

                    state._fsp--;


                            afterParserOrEnumRuleCall();
                        
                    otherlv_2=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_13); 

                        	newLeafNode(otherlv_2, grammarAccess.getOptionalModalPropertyValueAccess().getLeftParenthesisKeyword_1_1());
                        
                    // InternalJsonParser.g:1054:1: ( (otherlv_3= RULE_ID ) )
                    // InternalJsonParser.g:1055:1: (otherlv_3= RULE_ID )
                    {
                    // InternalJsonParser.g:1055:1: (otherlv_3= RULE_ID )
                    // InternalJsonParser.g:1056:3: otherlv_3= RULE_ID
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getOptionalModalPropertyValueRule());
                    	        }
                            
                    otherlv_3=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_19); 

                    		newLeafNode(otherlv_3, grammarAccess.getOptionalModalPropertyValueAccess().getInModeModeCrossReference_1_2_0()); 
                    	

                    }


                    }

                    // InternalJsonParser.g:1067:2: (otherlv_4= Comma ( (otherlv_5= RULE_ID ) ) )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==Comma) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // InternalJsonParser.g:1068:2: otherlv_4= Comma ( (otherlv_5= RULE_ID ) )
                    	    {
                    	    otherlv_4=(Token)match(input,Comma,FollowSets000.FOLLOW_13); 

                    	        	newLeafNode(otherlv_4, grammarAccess.getOptionalModalPropertyValueAccess().getCommaKeyword_1_3_0());
                    	        
                    	    // InternalJsonParser.g:1072:1: ( (otherlv_5= RULE_ID ) )
                    	    // InternalJsonParser.g:1073:1: (otherlv_5= RULE_ID )
                    	    {
                    	    // InternalJsonParser.g:1073:1: (otherlv_5= RULE_ID )
                    	    // InternalJsonParser.g:1074:3: otherlv_5= RULE_ID
                    	    {

                    	    			if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getOptionalModalPropertyValueRule());
                    	    	        }
                    	            
                    	    otherlv_5=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_19); 

                    	    		newLeafNode(otherlv_5, grammarAccess.getOptionalModalPropertyValueAccess().getInModeModeCrossReference_1_3_1_0()); 
                    	    	

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);

                    otherlv_6=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); 

                        	newLeafNode(otherlv_6, grammarAccess.getOptionalModalPropertyValueAccess().getRightParenthesisKeyword_1_4());
                        

                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOptionalModalPropertyValue"


    // $ANTLR start "entryRulePropertyValue"
    // InternalJsonParser.g:1098:1: entryRulePropertyValue returns [EObject current=null] : iv_rulePropertyValue= rulePropertyValue EOF ;
    public final EObject entryRulePropertyValue() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePropertyValue = null;


        try {
            // InternalJsonParser.g:1099:2: (iv_rulePropertyValue= rulePropertyValue EOF )
            // InternalJsonParser.g:1100:2: iv_rulePropertyValue= rulePropertyValue EOF
            {
             newCompositeNode(grammarAccess.getPropertyValueRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_rulePropertyValue=rulePropertyValue();

            state._fsp--;

             current =iv_rulePropertyValue; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePropertyValue"


    // $ANTLR start "rulePropertyValue"
    // InternalJsonParser.g:1107:1: rulePropertyValue returns [EObject current=null] : ( (lv_ownedValue_0_0= rulePropertyExpression ) ) ;
    public final EObject rulePropertyValue() throws RecognitionException {
        EObject current = null;

        EObject lv_ownedValue_0_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:1110:28: ( ( (lv_ownedValue_0_0= rulePropertyExpression ) ) )
            // InternalJsonParser.g:1111:1: ( (lv_ownedValue_0_0= rulePropertyExpression ) )
            {
            // InternalJsonParser.g:1111:1: ( (lv_ownedValue_0_0= rulePropertyExpression ) )
            // InternalJsonParser.g:1112:1: (lv_ownedValue_0_0= rulePropertyExpression )
            {
            // InternalJsonParser.g:1112:1: (lv_ownedValue_0_0= rulePropertyExpression )
            // InternalJsonParser.g:1113:3: lv_ownedValue_0_0= rulePropertyExpression
            {
             
            	        newCompositeNode(grammarAccess.getPropertyValueAccess().getOwnedValuePropertyExpressionParserRuleCall_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_2);
            lv_ownedValue_0_0=rulePropertyExpression();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getPropertyValueRule());
            	        }
                   		set(
                   			current, 
                   			"ownedValue",
                    		lv_ownedValue_0_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.PropertyExpression");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePropertyValue"


    // $ANTLR start "entryRulePropertyExpression"
    // InternalJsonParser.g:1137:1: entryRulePropertyExpression returns [EObject current=null] : iv_rulePropertyExpression= rulePropertyExpression EOF ;
    public final EObject entryRulePropertyExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePropertyExpression = null;


        try {
            // InternalJsonParser.g:1138:2: (iv_rulePropertyExpression= rulePropertyExpression EOF )
            // InternalJsonParser.g:1139:2: iv_rulePropertyExpression= rulePropertyExpression EOF
            {
             newCompositeNode(grammarAccess.getPropertyExpressionRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_rulePropertyExpression=rulePropertyExpression();

            state._fsp--;

             current =iv_rulePropertyExpression; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePropertyExpression"


    // $ANTLR start "rulePropertyExpression"
    // InternalJsonParser.g:1146:1: rulePropertyExpression returns [EObject current=null] : (this_RecordTerm_0= ruleRecordTerm | this_ReferenceTerm_1= ruleReferenceTerm | this_ComponentClassifierTerm_2= ruleComponentClassifierTerm | this_ComputedTerm_3= ruleComputedTerm | this_StringTerm_4= ruleStringTerm | this_NumericRangeTerm_5= ruleNumericRangeTerm | this_RealTerm_6= ruleRealTerm | this_IntegerTerm_7= ruleIntegerTerm | this_ListTerm_8= ruleListTerm | this_BooleanLiteral_9= ruleBooleanLiteral | this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm ) ;
    public final EObject rulePropertyExpression() throws RecognitionException {
        EObject current = null;

        EObject this_RecordTerm_0 = null;

        EObject this_ReferenceTerm_1 = null;

        EObject this_ComponentClassifierTerm_2 = null;

        EObject this_ComputedTerm_3 = null;

        EObject this_StringTerm_4 = null;

        EObject this_NumericRangeTerm_5 = null;

        EObject this_RealTerm_6 = null;

        EObject this_IntegerTerm_7 = null;

        EObject this_ListTerm_8 = null;

        EObject this_BooleanLiteral_9 = null;

        EObject this_LiteralorReferenceTerm_10 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:1149:28: ( (this_RecordTerm_0= ruleRecordTerm | this_ReferenceTerm_1= ruleReferenceTerm | this_ComponentClassifierTerm_2= ruleComponentClassifierTerm | this_ComputedTerm_3= ruleComputedTerm | this_StringTerm_4= ruleStringTerm | this_NumericRangeTerm_5= ruleNumericRangeTerm | this_RealTerm_6= ruleRealTerm | this_IntegerTerm_7= ruleIntegerTerm | this_ListTerm_8= ruleListTerm | this_BooleanLiteral_9= ruleBooleanLiteral | this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm ) )
            // InternalJsonParser.g:1150:1: (this_RecordTerm_0= ruleRecordTerm | this_ReferenceTerm_1= ruleReferenceTerm | this_ComponentClassifierTerm_2= ruleComponentClassifierTerm | this_ComputedTerm_3= ruleComputedTerm | this_StringTerm_4= ruleStringTerm | this_NumericRangeTerm_5= ruleNumericRangeTerm | this_RealTerm_6= ruleRealTerm | this_IntegerTerm_7= ruleIntegerTerm | this_ListTerm_8= ruleListTerm | this_BooleanLiteral_9= ruleBooleanLiteral | this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm )
            {
            // InternalJsonParser.g:1150:1: (this_RecordTerm_0= ruleRecordTerm | this_ReferenceTerm_1= ruleReferenceTerm | this_ComponentClassifierTerm_2= ruleComponentClassifierTerm | this_ComputedTerm_3= ruleComputedTerm | this_StringTerm_4= ruleStringTerm | this_NumericRangeTerm_5= ruleNumericRangeTerm | this_RealTerm_6= ruleRealTerm | this_IntegerTerm_7= ruleIntegerTerm | this_ListTerm_8= ruleListTerm | this_BooleanLiteral_9= ruleBooleanLiteral | this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm )
            int alt18=11;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // InternalJsonParser.g:1151:5: this_RecordTerm_0= ruleRecordTerm
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getRecordTermParserRuleCall_0()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_RecordTerm_0=ruleRecordTerm();

                    state._fsp--;


                            current = this_RecordTerm_0;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalJsonParser.g:1161:5: this_ReferenceTerm_1= ruleReferenceTerm
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getReferenceTermParserRuleCall_1()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ReferenceTerm_1=ruleReferenceTerm();

                    state._fsp--;


                            current = this_ReferenceTerm_1;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // InternalJsonParser.g:1171:5: this_ComponentClassifierTerm_2= ruleComponentClassifierTerm
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getComponentClassifierTermParserRuleCall_2()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ComponentClassifierTerm_2=ruleComponentClassifierTerm();

                    state._fsp--;


                            current = this_ComponentClassifierTerm_2;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // InternalJsonParser.g:1181:5: this_ComputedTerm_3= ruleComputedTerm
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getComputedTermParserRuleCall_3()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ComputedTerm_3=ruleComputedTerm();

                    state._fsp--;


                            current = this_ComputedTerm_3;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 5 :
                    // InternalJsonParser.g:1191:5: this_StringTerm_4= ruleStringTerm
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getStringTermParserRuleCall_4()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_StringTerm_4=ruleStringTerm();

                    state._fsp--;


                            current = this_StringTerm_4;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 6 :
                    // InternalJsonParser.g:1201:5: this_NumericRangeTerm_5= ruleNumericRangeTerm
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getNumericRangeTermParserRuleCall_5()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_NumericRangeTerm_5=ruleNumericRangeTerm();

                    state._fsp--;


                            current = this_NumericRangeTerm_5;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 7 :
                    // InternalJsonParser.g:1211:5: this_RealTerm_6= ruleRealTerm
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getRealTermParserRuleCall_6()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_RealTerm_6=ruleRealTerm();

                    state._fsp--;


                            current = this_RealTerm_6;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 8 :
                    // InternalJsonParser.g:1221:5: this_IntegerTerm_7= ruleIntegerTerm
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getIntegerTermParserRuleCall_7()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerTerm_7=ruleIntegerTerm();

                    state._fsp--;


                            current = this_IntegerTerm_7;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 9 :
                    // InternalJsonParser.g:1231:5: this_ListTerm_8= ruleListTerm
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getListTermParserRuleCall_8()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ListTerm_8=ruleListTerm();

                    state._fsp--;


                            current = this_ListTerm_8;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 10 :
                    // InternalJsonParser.g:1241:5: this_BooleanLiteral_9= ruleBooleanLiteral
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getBooleanLiteralParserRuleCall_9()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_BooleanLiteral_9=ruleBooleanLiteral();

                    state._fsp--;


                            current = this_BooleanLiteral_9;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 11 :
                    // InternalJsonParser.g:1251:5: this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm
                    {
                     
                            newCompositeNode(grammarAccess.getPropertyExpressionAccess().getLiteralorReferenceTermParserRuleCall_10()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_LiteralorReferenceTerm_10=ruleLiteralorReferenceTerm();

                    state._fsp--;


                            current = this_LiteralorReferenceTerm_10;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePropertyExpression"


    // $ANTLR start "entryRuleLiteralorReferenceTerm"
    // InternalJsonParser.g:1267:1: entryRuleLiteralorReferenceTerm returns [EObject current=null] : iv_ruleLiteralorReferenceTerm= ruleLiteralorReferenceTerm EOF ;
    public final EObject entryRuleLiteralorReferenceTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLiteralorReferenceTerm = null;


        try {
            // InternalJsonParser.g:1268:2: (iv_ruleLiteralorReferenceTerm= ruleLiteralorReferenceTerm EOF )
            // InternalJsonParser.g:1269:2: iv_ruleLiteralorReferenceTerm= ruleLiteralorReferenceTerm EOF
            {
             newCompositeNode(grammarAccess.getLiteralorReferenceTermRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleLiteralorReferenceTerm=ruleLiteralorReferenceTerm();

            state._fsp--;

             current =iv_ruleLiteralorReferenceTerm; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLiteralorReferenceTerm"


    // $ANTLR start "ruleLiteralorReferenceTerm"
    // InternalJsonParser.g:1276:1: ruleLiteralorReferenceTerm returns [EObject current=null] : ( ( ruleQPREF ) ) ;
    public final EObject ruleLiteralorReferenceTerm() throws RecognitionException {
        EObject current = null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:1279:28: ( ( ( ruleQPREF ) ) )
            // InternalJsonParser.g:1280:1: ( ( ruleQPREF ) )
            {
            // InternalJsonParser.g:1280:1: ( ( ruleQPREF ) )
            // InternalJsonParser.g:1281:1: ( ruleQPREF )
            {
            // InternalJsonParser.g:1281:1: ( ruleQPREF )
            // InternalJsonParser.g:1282:3: ruleQPREF
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getLiteralorReferenceTermRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getLiteralorReferenceTermAccess().getNamedValueAbstractNamedValueCrossReference_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_2);
            ruleQPREF();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLiteralorReferenceTerm"


    // $ANTLR start "entryRuleBooleanLiteral"
    // InternalJsonParser.g:1304:1: entryRuleBooleanLiteral returns [EObject current=null] : iv_ruleBooleanLiteral= ruleBooleanLiteral EOF ;
    public final EObject entryRuleBooleanLiteral() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBooleanLiteral = null;


        try {
            // InternalJsonParser.g:1305:2: (iv_ruleBooleanLiteral= ruleBooleanLiteral EOF )
            // InternalJsonParser.g:1306:2: iv_ruleBooleanLiteral= ruleBooleanLiteral EOF
            {
             newCompositeNode(grammarAccess.getBooleanLiteralRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleBooleanLiteral=ruleBooleanLiteral();

            state._fsp--;

             current =iv_ruleBooleanLiteral; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBooleanLiteral"


    // $ANTLR start "ruleBooleanLiteral"
    // InternalJsonParser.g:1313:1: ruleBooleanLiteral returns [EObject current=null] : ( () ( ( (lv_value_1_0= True ) ) | otherlv_2= False ) ) ;
    public final EObject ruleBooleanLiteral() throws RecognitionException {
        EObject current = null;

        Token lv_value_1_0=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:1316:28: ( ( () ( ( (lv_value_1_0= True ) ) | otherlv_2= False ) ) )
            // InternalJsonParser.g:1317:1: ( () ( ( (lv_value_1_0= True ) ) | otherlv_2= False ) )
            {
            // InternalJsonParser.g:1317:1: ( () ( ( (lv_value_1_0= True ) ) | otherlv_2= False ) )
            // InternalJsonParser.g:1317:2: () ( ( (lv_value_1_0= True ) ) | otherlv_2= False )
            {
            // InternalJsonParser.g:1317:2: ()
            // InternalJsonParser.g:1318:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getBooleanLiteralAccess().getBooleanLiteralAction_0(),
                        current);
                

            }

            // InternalJsonParser.g:1323:2: ( ( (lv_value_1_0= True ) ) | otherlv_2= False )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==True) ) {
                alt19=1;
            }
            else if ( (LA19_0==False) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // InternalJsonParser.g:1323:3: ( (lv_value_1_0= True ) )
                    {
                    // InternalJsonParser.g:1323:3: ( (lv_value_1_0= True ) )
                    // InternalJsonParser.g:1324:1: (lv_value_1_0= True )
                    {
                    // InternalJsonParser.g:1324:1: (lv_value_1_0= True )
                    // InternalJsonParser.g:1325:3: lv_value_1_0= True
                    {
                    lv_value_1_0=(Token)match(input,True,FollowSets000.FOLLOW_2); 

                            newLeafNode(lv_value_1_0, grammarAccess.getBooleanLiteralAccess().getValueTrueKeyword_1_0_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getBooleanLiteralRule());
                    	        }
                           		setWithLastConsumed(current, "value", true, "true");
                    	    

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalJsonParser.g:1341:2: otherlv_2= False
                    {
                    otherlv_2=(Token)match(input,False,FollowSets000.FOLLOW_2); 

                        	newLeafNode(otherlv_2, grammarAccess.getBooleanLiteralAccess().getFalseKeyword_1_1());
                        

                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBooleanLiteral"


    // $ANTLR start "entryRuleConstantValue"
    // InternalJsonParser.g:1353:1: entryRuleConstantValue returns [EObject current=null] : iv_ruleConstantValue= ruleConstantValue EOF ;
    public final EObject entryRuleConstantValue() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConstantValue = null;


        try {
            // InternalJsonParser.g:1354:2: (iv_ruleConstantValue= ruleConstantValue EOF )
            // InternalJsonParser.g:1355:2: iv_ruleConstantValue= ruleConstantValue EOF
            {
             newCompositeNode(grammarAccess.getConstantValueRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleConstantValue=ruleConstantValue();

            state._fsp--;

             current =iv_ruleConstantValue; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleConstantValue"


    // $ANTLR start "ruleConstantValue"
    // InternalJsonParser.g:1362:1: ruleConstantValue returns [EObject current=null] : ( ( ruleQPREF ) ) ;
    public final EObject ruleConstantValue() throws RecognitionException {
        EObject current = null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:1365:28: ( ( ( ruleQPREF ) ) )
            // InternalJsonParser.g:1366:1: ( ( ruleQPREF ) )
            {
            // InternalJsonParser.g:1366:1: ( ( ruleQPREF ) )
            // InternalJsonParser.g:1367:1: ( ruleQPREF )
            {
            // InternalJsonParser.g:1367:1: ( ruleQPREF )
            // InternalJsonParser.g:1368:3: ruleQPREF
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getConstantValueRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getConstantValueAccess().getNamedValuePropertyConstantCrossReference_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_2);
            ruleQPREF();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleConstantValue"


    // $ANTLR start "entryRuleReferenceTerm"
    // InternalJsonParser.g:1390:1: entryRuleReferenceTerm returns [EObject current=null] : iv_ruleReferenceTerm= ruleReferenceTerm EOF ;
    public final EObject entryRuleReferenceTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleReferenceTerm = null;


        try {
            // InternalJsonParser.g:1391:2: (iv_ruleReferenceTerm= ruleReferenceTerm EOF )
            // InternalJsonParser.g:1392:2: iv_ruleReferenceTerm= ruleReferenceTerm EOF
            {
             newCompositeNode(grammarAccess.getReferenceTermRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleReferenceTerm=ruleReferenceTerm();

            state._fsp--;

             current =iv_ruleReferenceTerm; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleReferenceTerm"


    // $ANTLR start "ruleReferenceTerm"
    // InternalJsonParser.g:1399:1: ruleReferenceTerm returns [EObject current=null] : (otherlv_0= Reference otherlv_1= LeftParenthesis ( (lv_path_2_0= ruleContainmentPathElement ) ) otherlv_3= RightParenthesis ) ;
    public final EObject ruleReferenceTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_path_2_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:1402:28: ( (otherlv_0= Reference otherlv_1= LeftParenthesis ( (lv_path_2_0= ruleContainmentPathElement ) ) otherlv_3= RightParenthesis ) )
            // InternalJsonParser.g:1403:1: (otherlv_0= Reference otherlv_1= LeftParenthesis ( (lv_path_2_0= ruleContainmentPathElement ) ) otherlv_3= RightParenthesis )
            {
            // InternalJsonParser.g:1403:1: (otherlv_0= Reference otherlv_1= LeftParenthesis ( (lv_path_2_0= ruleContainmentPathElement ) ) otherlv_3= RightParenthesis )
            // InternalJsonParser.g:1404:2: otherlv_0= Reference otherlv_1= LeftParenthesis ( (lv_path_2_0= ruleContainmentPathElement ) ) otherlv_3= RightParenthesis
            {
            otherlv_0=(Token)match(input,Reference,FollowSets000.FOLLOW_15); 

                	newLeafNode(otherlv_0, grammarAccess.getReferenceTermAccess().getReferenceKeyword_0());
                
            otherlv_1=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_13); 

                	newLeafNode(otherlv_1, grammarAccess.getReferenceTermAccess().getLeftParenthesisKeyword_1());
                
            // InternalJsonParser.g:1413:1: ( (lv_path_2_0= ruleContainmentPathElement ) )
            // InternalJsonParser.g:1414:1: (lv_path_2_0= ruleContainmentPathElement )
            {
            // InternalJsonParser.g:1414:1: (lv_path_2_0= ruleContainmentPathElement )
            // InternalJsonParser.g:1415:3: lv_path_2_0= ruleContainmentPathElement
            {
             
            	        newCompositeNode(grammarAccess.getReferenceTermAccess().getPathContainmentPathElementParserRuleCall_2_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_16);
            lv_path_2_0=ruleContainmentPathElement();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getReferenceTermRule());
            	        }
                   		set(
                   			current, 
                   			"path",
                    		lv_path_2_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.ContainmentPathElement");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_3=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_3, grammarAccess.getReferenceTermAccess().getRightParenthesisKeyword_3());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleReferenceTerm"


    // $ANTLR start "entryRuleRecordTerm"
    // InternalJsonParser.g:1444:1: entryRuleRecordTerm returns [EObject current=null] : iv_ruleRecordTerm= ruleRecordTerm EOF ;
    public final EObject entryRuleRecordTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRecordTerm = null;


        try {
            // InternalJsonParser.g:1445:2: (iv_ruleRecordTerm= ruleRecordTerm EOF )
            // InternalJsonParser.g:1446:2: iv_ruleRecordTerm= ruleRecordTerm EOF
            {
             newCompositeNode(grammarAccess.getRecordTermRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleRecordTerm=ruleRecordTerm();

            state._fsp--;

             current =iv_ruleRecordTerm; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRecordTerm"


    // $ANTLR start "ruleRecordTerm"
    // InternalJsonParser.g:1453:1: ruleRecordTerm returns [EObject current=null] : (otherlv_0= LeftSquareBracket ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+ otherlv_2= RightSquareBracket ) ;
    public final EObject ruleRecordTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        EObject lv_ownedFieldValue_1_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:1456:28: ( (otherlv_0= LeftSquareBracket ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+ otherlv_2= RightSquareBracket ) )
            // InternalJsonParser.g:1457:1: (otherlv_0= LeftSquareBracket ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+ otherlv_2= RightSquareBracket )
            {
            // InternalJsonParser.g:1457:1: (otherlv_0= LeftSquareBracket ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+ otherlv_2= RightSquareBracket )
            // InternalJsonParser.g:1458:2: otherlv_0= LeftSquareBracket ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+ otherlv_2= RightSquareBracket
            {
            otherlv_0=(Token)match(input,LeftSquareBracket,FollowSets000.FOLLOW_13); 

                	newLeafNode(otherlv_0, grammarAccess.getRecordTermAccess().getLeftSquareBracketKeyword_0());
                
            // InternalJsonParser.g:1462:1: ( (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation ) )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==RULE_ID) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // InternalJsonParser.g:1463:1: (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation )
            	    {
            	    // InternalJsonParser.g:1463:1: (lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation )
            	    // InternalJsonParser.g:1464:3: lv_ownedFieldValue_1_0= ruleFieldPropertyAssociation
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getRecordTermAccess().getOwnedFieldValueFieldPropertyAssociationParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FollowSets000.FOLLOW_20);
            	    lv_ownedFieldValue_1_0=ruleFieldPropertyAssociation();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getRecordTermRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"ownedFieldValue",
            	            		lv_ownedFieldValue_1_0, 
            	            		"org.osate.xtext.aadl2.properties.Properties.FieldPropertyAssociation");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);

            otherlv_2=(Token)match(input,RightSquareBracket,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_2, grammarAccess.getRecordTermAccess().getRightSquareBracketKeyword_2());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRecordTerm"


    // $ANTLR start "entryRuleComputedTerm"
    // InternalJsonParser.g:1495:1: entryRuleComputedTerm returns [EObject current=null] : iv_ruleComputedTerm= ruleComputedTerm EOF ;
    public final EObject entryRuleComputedTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComputedTerm = null;


        try {
            // InternalJsonParser.g:1496:2: (iv_ruleComputedTerm= ruleComputedTerm EOF )
            // InternalJsonParser.g:1497:2: iv_ruleComputedTerm= ruleComputedTerm EOF
            {
             newCompositeNode(grammarAccess.getComputedTermRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleComputedTerm=ruleComputedTerm();

            state._fsp--;

             current =iv_ruleComputedTerm; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleComputedTerm"


    // $ANTLR start "ruleComputedTerm"
    // InternalJsonParser.g:1504:1: ruleComputedTerm returns [EObject current=null] : (otherlv_0= Compute otherlv_1= LeftParenthesis ( (lv_function_2_0= RULE_ID ) ) otherlv_3= RightParenthesis ) ;
    public final EObject ruleComputedTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token lv_function_2_0=null;
        Token otherlv_3=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:1507:28: ( (otherlv_0= Compute otherlv_1= LeftParenthesis ( (lv_function_2_0= RULE_ID ) ) otherlv_3= RightParenthesis ) )
            // InternalJsonParser.g:1508:1: (otherlv_0= Compute otherlv_1= LeftParenthesis ( (lv_function_2_0= RULE_ID ) ) otherlv_3= RightParenthesis )
            {
            // InternalJsonParser.g:1508:1: (otherlv_0= Compute otherlv_1= LeftParenthesis ( (lv_function_2_0= RULE_ID ) ) otherlv_3= RightParenthesis )
            // InternalJsonParser.g:1509:2: otherlv_0= Compute otherlv_1= LeftParenthesis ( (lv_function_2_0= RULE_ID ) ) otherlv_3= RightParenthesis
            {
            otherlv_0=(Token)match(input,Compute,FollowSets000.FOLLOW_15); 

                	newLeafNode(otherlv_0, grammarAccess.getComputedTermAccess().getComputeKeyword_0());
                
            otherlv_1=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_13); 

                	newLeafNode(otherlv_1, grammarAccess.getComputedTermAccess().getLeftParenthesisKeyword_1());
                
            // InternalJsonParser.g:1518:1: ( (lv_function_2_0= RULE_ID ) )
            // InternalJsonParser.g:1519:1: (lv_function_2_0= RULE_ID )
            {
            // InternalJsonParser.g:1519:1: (lv_function_2_0= RULE_ID )
            // InternalJsonParser.g:1520:3: lv_function_2_0= RULE_ID
            {
            lv_function_2_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_16); 

            			newLeafNode(lv_function_2_0, grammarAccess.getComputedTermAccess().getFunctionIDTerminalRuleCall_2_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getComputedTermRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"function",
                    		lv_function_2_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.ID");
            	    

            }


            }

            otherlv_3=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_3, grammarAccess.getComputedTermAccess().getRightParenthesisKeyword_3());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleComputedTerm"


    // $ANTLR start "entryRuleComponentClassifierTerm"
    // InternalJsonParser.g:1549:1: entryRuleComponentClassifierTerm returns [EObject current=null] : iv_ruleComponentClassifierTerm= ruleComponentClassifierTerm EOF ;
    public final EObject entryRuleComponentClassifierTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComponentClassifierTerm = null;


        try {
            // InternalJsonParser.g:1550:2: (iv_ruleComponentClassifierTerm= ruleComponentClassifierTerm EOF )
            // InternalJsonParser.g:1551:2: iv_ruleComponentClassifierTerm= ruleComponentClassifierTerm EOF
            {
             newCompositeNode(grammarAccess.getComponentClassifierTermRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleComponentClassifierTerm=ruleComponentClassifierTerm();

            state._fsp--;

             current =iv_ruleComponentClassifierTerm; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleComponentClassifierTerm"


    // $ANTLR start "ruleComponentClassifierTerm"
    // InternalJsonParser.g:1558:1: ruleComponentClassifierTerm returns [EObject current=null] : (otherlv_0= Classifier otherlv_1= LeftParenthesis ( ( ruleQCREF ) ) otherlv_3= RightParenthesis ) ;
    public final EObject ruleComponentClassifierTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:1561:28: ( (otherlv_0= Classifier otherlv_1= LeftParenthesis ( ( ruleQCREF ) ) otherlv_3= RightParenthesis ) )
            // InternalJsonParser.g:1562:1: (otherlv_0= Classifier otherlv_1= LeftParenthesis ( ( ruleQCREF ) ) otherlv_3= RightParenthesis )
            {
            // InternalJsonParser.g:1562:1: (otherlv_0= Classifier otherlv_1= LeftParenthesis ( ( ruleQCREF ) ) otherlv_3= RightParenthesis )
            // InternalJsonParser.g:1563:2: otherlv_0= Classifier otherlv_1= LeftParenthesis ( ( ruleQCREF ) ) otherlv_3= RightParenthesis
            {
            otherlv_0=(Token)match(input,Classifier,FollowSets000.FOLLOW_15); 

                	newLeafNode(otherlv_0, grammarAccess.getComponentClassifierTermAccess().getClassifierKeyword_0());
                
            otherlv_1=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_13); 

                	newLeafNode(otherlv_1, grammarAccess.getComponentClassifierTermAccess().getLeftParenthesisKeyword_1());
                
            // InternalJsonParser.g:1572:1: ( ( ruleQCREF ) )
            // InternalJsonParser.g:1573:1: ( ruleQCREF )
            {
            // InternalJsonParser.g:1573:1: ( ruleQCREF )
            // InternalJsonParser.g:1574:3: ruleQCREF
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getComponentClassifierTermRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getComponentClassifierTermAccess().getClassifierComponentClassifierCrossReference_2_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_16);
            ruleQCREF();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_3=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_3, grammarAccess.getComponentClassifierTermAccess().getRightParenthesisKeyword_3());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleComponentClassifierTerm"


    // $ANTLR start "entryRuleListTerm"
    // InternalJsonParser.g:1601:1: entryRuleListTerm returns [EObject current=null] : iv_ruleListTerm= ruleListTerm EOF ;
    public final EObject entryRuleListTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleListTerm = null;


        try {
            // InternalJsonParser.g:1602:2: (iv_ruleListTerm= ruleListTerm EOF )
            // InternalJsonParser.g:1603:2: iv_ruleListTerm= ruleListTerm EOF
            {
             newCompositeNode(grammarAccess.getListTermRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleListTerm=ruleListTerm();

            state._fsp--;

             current =iv_ruleListTerm; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleListTerm"


    // $ANTLR start "ruleListTerm"
    // InternalJsonParser.g:1610:1: ruleListTerm returns [EObject current=null] : ( () otherlv_1= LeftParenthesis ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )? otherlv_5= RightParenthesis ) ;
    public final EObject ruleListTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_ownedListElement_2_0 = null;

        EObject lv_ownedListElement_4_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:1613:28: ( ( () otherlv_1= LeftParenthesis ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )? otherlv_5= RightParenthesis ) )
            // InternalJsonParser.g:1614:1: ( () otherlv_1= LeftParenthesis ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )? otherlv_5= RightParenthesis )
            {
            // InternalJsonParser.g:1614:1: ( () otherlv_1= LeftParenthesis ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )? otherlv_5= RightParenthesis )
            // InternalJsonParser.g:1614:2: () otherlv_1= LeftParenthesis ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )? otherlv_5= RightParenthesis
            {
            // InternalJsonParser.g:1614:2: ()
            // InternalJsonParser.g:1615:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getListTermAccess().getListValueAction_0(),
                        current);
                

            }

            otherlv_1=(Token)match(input,LeftParenthesis,FollowSets000.FOLLOW_21); 

                	newLeafNode(otherlv_1, grammarAccess.getListTermAccess().getLeftParenthesisKeyword_1());
                
            // InternalJsonParser.g:1625:1: ( ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )* )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( ((LA22_0>=Classifier && LA22_0<=Reference)||LA22_0==Compute||LA22_0==False||LA22_0==True||LA22_0==LeftParenthesis||LA22_0==PlusSign||LA22_0==HyphenMinus||LA22_0==LeftSquareBracket||(LA22_0>=RULE_REAL_NUMBER && LA22_0<=RULE_STRING)||LA22_0==RULE_INTEGER_LIT||LA22_0==RULE_ID) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // InternalJsonParser.g:1625:2: ( (lv_ownedListElement_2_0= rulePropertyExpression ) ) (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )*
                    {
                    // InternalJsonParser.g:1625:2: ( (lv_ownedListElement_2_0= rulePropertyExpression ) )
                    // InternalJsonParser.g:1626:1: (lv_ownedListElement_2_0= rulePropertyExpression )
                    {
                    // InternalJsonParser.g:1626:1: (lv_ownedListElement_2_0= rulePropertyExpression )
                    // InternalJsonParser.g:1627:3: lv_ownedListElement_2_0= rulePropertyExpression
                    {
                     
                    	        newCompositeNode(grammarAccess.getListTermAccess().getOwnedListElementPropertyExpressionParserRuleCall_2_0_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_19);
                    lv_ownedListElement_2_0=rulePropertyExpression();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getListTermRule());
                    	        }
                           		add(
                           			current, 
                           			"ownedListElement",
                            		lv_ownedListElement_2_0, 
                            		"org.osate.xtext.aadl2.properties.Properties.PropertyExpression");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }

                    // InternalJsonParser.g:1643:2: (otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) ) )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( (LA21_0==Comma) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // InternalJsonParser.g:1644:2: otherlv_3= Comma ( (lv_ownedListElement_4_0= rulePropertyExpression ) )
                    	    {
                    	    otherlv_3=(Token)match(input,Comma,FollowSets000.FOLLOW_11); 

                    	        	newLeafNode(otherlv_3, grammarAccess.getListTermAccess().getCommaKeyword_2_1_0());
                    	        
                    	    // InternalJsonParser.g:1648:1: ( (lv_ownedListElement_4_0= rulePropertyExpression ) )
                    	    // InternalJsonParser.g:1649:1: (lv_ownedListElement_4_0= rulePropertyExpression )
                    	    {
                    	    // InternalJsonParser.g:1649:1: (lv_ownedListElement_4_0= rulePropertyExpression )
                    	    // InternalJsonParser.g:1650:3: lv_ownedListElement_4_0= rulePropertyExpression
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getListTermAccess().getOwnedListElementPropertyExpressionParserRuleCall_2_1_1_0()); 
                    	    	    
                    	    pushFollow(FollowSets000.FOLLOW_19);
                    	    lv_ownedListElement_4_0=rulePropertyExpression();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getListTermRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"ownedListElement",
                    	            		lv_ownedListElement_4_0, 
                    	            		"org.osate.xtext.aadl2.properties.Properties.PropertyExpression");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_5=(Token)match(input,RightParenthesis,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_5, grammarAccess.getListTermAccess().getRightParenthesisKeyword_3());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleListTerm"


    // $ANTLR start "entryRuleFieldPropertyAssociation"
    // InternalJsonParser.g:1679:1: entryRuleFieldPropertyAssociation returns [EObject current=null] : iv_ruleFieldPropertyAssociation= ruleFieldPropertyAssociation EOF ;
    public final EObject entryRuleFieldPropertyAssociation() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFieldPropertyAssociation = null;


        try {
            // InternalJsonParser.g:1680:2: (iv_ruleFieldPropertyAssociation= ruleFieldPropertyAssociation EOF )
            // InternalJsonParser.g:1681:2: iv_ruleFieldPropertyAssociation= ruleFieldPropertyAssociation EOF
            {
             newCompositeNode(grammarAccess.getFieldPropertyAssociationRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleFieldPropertyAssociation=ruleFieldPropertyAssociation();

            state._fsp--;

             current =iv_ruleFieldPropertyAssociation; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleFieldPropertyAssociation"


    // $ANTLR start "ruleFieldPropertyAssociation"
    // InternalJsonParser.g:1688:1: ruleFieldPropertyAssociation returns [EObject current=null] : ( ( (otherlv_0= RULE_ID ) ) otherlv_1= EqualsSignGreaterThanSign ( (lv_ownedValue_2_0= rulePropertyExpression ) ) otherlv_3= Semicolon ) ;
    public final EObject ruleFieldPropertyAssociation() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_ownedValue_2_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:1691:28: ( ( ( (otherlv_0= RULE_ID ) ) otherlv_1= EqualsSignGreaterThanSign ( (lv_ownedValue_2_0= rulePropertyExpression ) ) otherlv_3= Semicolon ) )
            // InternalJsonParser.g:1692:1: ( ( (otherlv_0= RULE_ID ) ) otherlv_1= EqualsSignGreaterThanSign ( (lv_ownedValue_2_0= rulePropertyExpression ) ) otherlv_3= Semicolon )
            {
            // InternalJsonParser.g:1692:1: ( ( (otherlv_0= RULE_ID ) ) otherlv_1= EqualsSignGreaterThanSign ( (lv_ownedValue_2_0= rulePropertyExpression ) ) otherlv_3= Semicolon )
            // InternalJsonParser.g:1692:2: ( (otherlv_0= RULE_ID ) ) otherlv_1= EqualsSignGreaterThanSign ( (lv_ownedValue_2_0= rulePropertyExpression ) ) otherlv_3= Semicolon
            {
            // InternalJsonParser.g:1692:2: ( (otherlv_0= RULE_ID ) )
            // InternalJsonParser.g:1693:1: (otherlv_0= RULE_ID )
            {
            // InternalJsonParser.g:1693:1: (otherlv_0= RULE_ID )
            // InternalJsonParser.g:1694:3: otherlv_0= RULE_ID
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getFieldPropertyAssociationRule());
            	        }
                    
            otherlv_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_22); 

            		newLeafNode(otherlv_0, grammarAccess.getFieldPropertyAssociationAccess().getPropertyBasicPropertyCrossReference_0_0()); 
            	

            }


            }

            otherlv_1=(Token)match(input,EqualsSignGreaterThanSign,FollowSets000.FOLLOW_11); 

                	newLeafNode(otherlv_1, grammarAccess.getFieldPropertyAssociationAccess().getEqualsSignGreaterThanSignKeyword_1());
                
            // InternalJsonParser.g:1710:1: ( (lv_ownedValue_2_0= rulePropertyExpression ) )
            // InternalJsonParser.g:1711:1: (lv_ownedValue_2_0= rulePropertyExpression )
            {
            // InternalJsonParser.g:1711:1: (lv_ownedValue_2_0= rulePropertyExpression )
            // InternalJsonParser.g:1712:3: lv_ownedValue_2_0= rulePropertyExpression
            {
             
            	        newCompositeNode(grammarAccess.getFieldPropertyAssociationAccess().getOwnedValuePropertyExpressionParserRuleCall_2_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_17);
            lv_ownedValue_2_0=rulePropertyExpression();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getFieldPropertyAssociationRule());
            	        }
                   		set(
                   			current, 
                   			"ownedValue",
                    		lv_ownedValue_2_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.PropertyExpression");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_3=(Token)match(input,Semicolon,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_3, grammarAccess.getFieldPropertyAssociationAccess().getSemicolonKeyword_3());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleFieldPropertyAssociation"


    // $ANTLR start "entryRuleContainmentPathElement"
    // InternalJsonParser.g:1741:1: entryRuleContainmentPathElement returns [EObject current=null] : iv_ruleContainmentPathElement= ruleContainmentPathElement EOF ;
    public final EObject entryRuleContainmentPathElement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleContainmentPathElement = null;


        try {
            // InternalJsonParser.g:1742:2: (iv_ruleContainmentPathElement= ruleContainmentPathElement EOF )
            // InternalJsonParser.g:1743:2: iv_ruleContainmentPathElement= ruleContainmentPathElement EOF
            {
             newCompositeNode(grammarAccess.getContainmentPathElementRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleContainmentPathElement=ruleContainmentPathElement();

            state._fsp--;

             current =iv_ruleContainmentPathElement; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleContainmentPathElement"


    // $ANTLR start "ruleContainmentPathElement"
    // InternalJsonParser.g:1750:1: ruleContainmentPathElement returns [EObject current=null] : ( ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* ) (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )? ) ;
    public final EObject ruleContainmentPathElement() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        EObject lv_arrayRange_1_0 = null;

        EObject lv_path_3_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:1753:28: ( ( ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* ) (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )? ) )
            // InternalJsonParser.g:1754:1: ( ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* ) (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )? )
            {
            // InternalJsonParser.g:1754:1: ( ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* ) (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )? )
            // InternalJsonParser.g:1754:2: ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* ) (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )?
            {
            // InternalJsonParser.g:1754:2: ( ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )* )
            // InternalJsonParser.g:1754:3: ( (otherlv_0= RULE_ID ) ) ( (lv_arrayRange_1_0= ruleArrayRange ) )*
            {
            // InternalJsonParser.g:1754:3: ( (otherlv_0= RULE_ID ) )
            // InternalJsonParser.g:1755:1: (otherlv_0= RULE_ID )
            {
            // InternalJsonParser.g:1755:1: (otherlv_0= RULE_ID )
            // InternalJsonParser.g:1756:3: otherlv_0= RULE_ID
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getContainmentPathElementRule());
            	        }
                    
            otherlv_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_23); 

            		newLeafNode(otherlv_0, grammarAccess.getContainmentPathElementAccess().getNamedElementNamedElementCrossReference_0_0_0()); 
            	

            }


            }

            // InternalJsonParser.g:1767:2: ( (lv_arrayRange_1_0= ruleArrayRange ) )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==LeftSquareBracket) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // InternalJsonParser.g:1768:1: (lv_arrayRange_1_0= ruleArrayRange )
            	    {
            	    // InternalJsonParser.g:1768:1: (lv_arrayRange_1_0= ruleArrayRange )
            	    // InternalJsonParser.g:1769:3: lv_arrayRange_1_0= ruleArrayRange
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getContainmentPathElementAccess().getArrayRangeArrayRangeParserRuleCall_0_1_0()); 
            	    	    
            	    pushFollow(FollowSets000.FOLLOW_23);
            	    lv_arrayRange_1_0=ruleArrayRange();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getContainmentPathElementRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"arrayRange",
            	            		lv_arrayRange_1_0, 
            	            		"org.osate.xtext.aadl2.properties.Properties.ArrayRange");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);


            }

            // InternalJsonParser.g:1785:4: (otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) ) )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==FullStop) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // InternalJsonParser.g:1786:2: otherlv_2= FullStop ( (lv_path_3_0= ruleContainmentPathElement ) )
                    {
                    otherlv_2=(Token)match(input,FullStop,FollowSets000.FOLLOW_13); 

                        	newLeafNode(otherlv_2, grammarAccess.getContainmentPathElementAccess().getFullStopKeyword_1_0());
                        
                    // InternalJsonParser.g:1790:1: ( (lv_path_3_0= ruleContainmentPathElement ) )
                    // InternalJsonParser.g:1791:1: (lv_path_3_0= ruleContainmentPathElement )
                    {
                    // InternalJsonParser.g:1791:1: (lv_path_3_0= ruleContainmentPathElement )
                    // InternalJsonParser.g:1792:3: lv_path_3_0= ruleContainmentPathElement
                    {
                     
                    	        newCompositeNode(grammarAccess.getContainmentPathElementAccess().getPathContainmentPathElementParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_path_3_0=ruleContainmentPathElement();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getContainmentPathElementRule());
                    	        }
                           		set(
                           			current, 
                           			"path",
                            		lv_path_3_0, 
                            		"org.osate.xtext.aadl2.properties.Properties.ContainmentPathElement");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleContainmentPathElement"


    // $ANTLR start "entryRulePlusMinus"
    // InternalJsonParser.g:1818:1: entryRulePlusMinus returns [String current=null] : iv_rulePlusMinus= rulePlusMinus EOF ;
    public final String entryRulePlusMinus() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePlusMinus = null;


        try {
            // InternalJsonParser.g:1819:1: (iv_rulePlusMinus= rulePlusMinus EOF )
            // InternalJsonParser.g:1820:2: iv_rulePlusMinus= rulePlusMinus EOF
            {
             newCompositeNode(grammarAccess.getPlusMinusRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_rulePlusMinus=rulePlusMinus();

            state._fsp--;

             current =iv_rulePlusMinus.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePlusMinus"


    // $ANTLR start "rulePlusMinus"
    // InternalJsonParser.g:1827:1: rulePlusMinus returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= PlusSign | kw= HyphenMinus ) ;
    public final AntlrDatatypeRuleToken rulePlusMinus() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:1831:6: ( (kw= PlusSign | kw= HyphenMinus ) )
            // InternalJsonParser.g:1832:1: (kw= PlusSign | kw= HyphenMinus )
            {
            // InternalJsonParser.g:1832:1: (kw= PlusSign | kw= HyphenMinus )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==PlusSign) ) {
                alt25=1;
            }
            else if ( (LA25_0==HyphenMinus) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // InternalJsonParser.g:1833:2: kw= PlusSign
                    {
                    kw=(Token)match(input,PlusSign,FollowSets000.FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPlusMinusAccess().getPlusSignKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalJsonParser.g:1840:2: kw= HyphenMinus
                    {
                    kw=(Token)match(input,HyphenMinus,FollowSets000.FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPlusMinusAccess().getHyphenMinusKeyword_1()); 
                        

                    }
                    break;

            }


            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePlusMinus"


    // $ANTLR start "entryRuleStringTerm"
    // InternalJsonParser.g:1853:1: entryRuleStringTerm returns [EObject current=null] : iv_ruleStringTerm= ruleStringTerm EOF ;
    public final EObject entryRuleStringTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStringTerm = null;


        try {
            // InternalJsonParser.g:1854:2: (iv_ruleStringTerm= ruleStringTerm EOF )
            // InternalJsonParser.g:1855:2: iv_ruleStringTerm= ruleStringTerm EOF
            {
             newCompositeNode(grammarAccess.getStringTermRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleStringTerm=ruleStringTerm();

            state._fsp--;

             current =iv_ruleStringTerm; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStringTerm"


    // $ANTLR start "ruleStringTerm"
    // InternalJsonParser.g:1862:1: ruleStringTerm returns [EObject current=null] : ( (lv_value_0_0= ruleNoQuoteString ) ) ;
    public final EObject ruleStringTerm() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_value_0_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:1865:28: ( ( (lv_value_0_0= ruleNoQuoteString ) ) )
            // InternalJsonParser.g:1866:1: ( (lv_value_0_0= ruleNoQuoteString ) )
            {
            // InternalJsonParser.g:1866:1: ( (lv_value_0_0= ruleNoQuoteString ) )
            // InternalJsonParser.g:1867:1: (lv_value_0_0= ruleNoQuoteString )
            {
            // InternalJsonParser.g:1867:1: (lv_value_0_0= ruleNoQuoteString )
            // InternalJsonParser.g:1868:3: lv_value_0_0= ruleNoQuoteString
            {
             
            	        newCompositeNode(grammarAccess.getStringTermAccess().getValueNoQuoteStringParserRuleCall_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_2);
            lv_value_0_0=ruleNoQuoteString();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getStringTermRule());
            	        }
                   		set(
                   			current, 
                   			"value",
                    		lv_value_0_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.NoQuoteString");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStringTerm"


    // $ANTLR start "entryRuleNoQuoteString"
    // InternalJsonParser.g:1892:1: entryRuleNoQuoteString returns [String current=null] : iv_ruleNoQuoteString= ruleNoQuoteString EOF ;
    public final String entryRuleNoQuoteString() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNoQuoteString = null;


        try {
            // InternalJsonParser.g:1893:1: (iv_ruleNoQuoteString= ruleNoQuoteString EOF )
            // InternalJsonParser.g:1894:2: iv_ruleNoQuoteString= ruleNoQuoteString EOF
            {
             newCompositeNode(grammarAccess.getNoQuoteStringRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNoQuoteString=ruleNoQuoteString();

            state._fsp--;

             current =iv_ruleNoQuoteString.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNoQuoteString"


    // $ANTLR start "ruleNoQuoteString"
    // InternalJsonParser.g:1901:1: ruleNoQuoteString returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleNoQuoteString() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:1905:6: (this_STRING_0= RULE_STRING )
            // InternalJsonParser.g:1906:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FollowSets000.FOLLOW_2); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getNoQuoteStringAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNoQuoteString"


    // $ANTLR start "entryRuleArrayRange"
    // InternalJsonParser.g:1921:1: entryRuleArrayRange returns [EObject current=null] : iv_ruleArrayRange= ruleArrayRange EOF ;
    public final EObject entryRuleArrayRange() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArrayRange = null;


        try {
            // InternalJsonParser.g:1922:2: (iv_ruleArrayRange= ruleArrayRange EOF )
            // InternalJsonParser.g:1923:2: iv_ruleArrayRange= ruleArrayRange EOF
            {
             newCompositeNode(grammarAccess.getArrayRangeRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleArrayRange=ruleArrayRange();

            state._fsp--;

             current =iv_ruleArrayRange; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleArrayRange"


    // $ANTLR start "ruleArrayRange"
    // InternalJsonParser.g:1930:1: ruleArrayRange returns [EObject current=null] : ( () otherlv_1= LeftSquareBracket ( (lv_lowerBound_2_0= ruleINTVALUE ) ) (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )? otherlv_5= RightSquareBracket ) ;
    public final EObject ruleArrayRange() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        AntlrDatatypeRuleToken lv_lowerBound_2_0 = null;

        AntlrDatatypeRuleToken lv_upperBound_4_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:1933:28: ( ( () otherlv_1= LeftSquareBracket ( (lv_lowerBound_2_0= ruleINTVALUE ) ) (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )? otherlv_5= RightSquareBracket ) )
            // InternalJsonParser.g:1934:1: ( () otherlv_1= LeftSquareBracket ( (lv_lowerBound_2_0= ruleINTVALUE ) ) (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )? otherlv_5= RightSquareBracket )
            {
            // InternalJsonParser.g:1934:1: ( () otherlv_1= LeftSquareBracket ( (lv_lowerBound_2_0= ruleINTVALUE ) ) (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )? otherlv_5= RightSquareBracket )
            // InternalJsonParser.g:1934:2: () otherlv_1= LeftSquareBracket ( (lv_lowerBound_2_0= ruleINTVALUE ) ) (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )? otherlv_5= RightSquareBracket
            {
            // InternalJsonParser.g:1934:2: ()
            // InternalJsonParser.g:1935:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getArrayRangeAccess().getArrayRangeAction_0(),
                        current);
                

            }

            otherlv_1=(Token)match(input,LeftSquareBracket,FollowSets000.FOLLOW_24); 

                	newLeafNode(otherlv_1, grammarAccess.getArrayRangeAccess().getLeftSquareBracketKeyword_1());
                
            // InternalJsonParser.g:1945:1: ( (lv_lowerBound_2_0= ruleINTVALUE ) )
            // InternalJsonParser.g:1946:1: (lv_lowerBound_2_0= ruleINTVALUE )
            {
            // InternalJsonParser.g:1946:1: (lv_lowerBound_2_0= ruleINTVALUE )
            // InternalJsonParser.g:1947:3: lv_lowerBound_2_0= ruleINTVALUE
            {
             
            	        newCompositeNode(grammarAccess.getArrayRangeAccess().getLowerBoundINTVALUEParserRuleCall_2_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_25);
            lv_lowerBound_2_0=ruleINTVALUE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getArrayRangeRule());
            	        }
                   		set(
                   			current, 
                   			"lowerBound",
                    		lv_lowerBound_2_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.INTVALUE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalJsonParser.g:1963:2: (otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==FullStopFullStop) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // InternalJsonParser.g:1964:2: otherlv_3= FullStopFullStop ( (lv_upperBound_4_0= ruleINTVALUE ) )
                    {
                    otherlv_3=(Token)match(input,FullStopFullStop,FollowSets000.FOLLOW_24); 

                        	newLeafNode(otherlv_3, grammarAccess.getArrayRangeAccess().getFullStopFullStopKeyword_3_0());
                        
                    // InternalJsonParser.g:1968:1: ( (lv_upperBound_4_0= ruleINTVALUE ) )
                    // InternalJsonParser.g:1969:1: (lv_upperBound_4_0= ruleINTVALUE )
                    {
                    // InternalJsonParser.g:1969:1: (lv_upperBound_4_0= ruleINTVALUE )
                    // InternalJsonParser.g:1970:3: lv_upperBound_4_0= ruleINTVALUE
                    {
                     
                    	        newCompositeNode(grammarAccess.getArrayRangeAccess().getUpperBoundINTVALUEParserRuleCall_3_1_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_26);
                    lv_upperBound_4_0=ruleINTVALUE();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getArrayRangeRule());
                    	        }
                           		set(
                           			current, 
                           			"upperBound",
                            		lv_upperBound_4_0, 
                            		"org.osate.xtext.aadl2.properties.Properties.INTVALUE");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_5=(Token)match(input,RightSquareBracket,FollowSets000.FOLLOW_2); 

                	newLeafNode(otherlv_5, grammarAccess.getArrayRangeAccess().getRightSquareBracketKeyword_4());
                

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleArrayRange"


    // $ANTLR start "entryRuleSignedConstant"
    // InternalJsonParser.g:1999:1: entryRuleSignedConstant returns [EObject current=null] : iv_ruleSignedConstant= ruleSignedConstant EOF ;
    public final EObject entryRuleSignedConstant() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSignedConstant = null;


        try {
            // InternalJsonParser.g:2000:2: (iv_ruleSignedConstant= ruleSignedConstant EOF )
            // InternalJsonParser.g:2001:2: iv_ruleSignedConstant= ruleSignedConstant EOF
            {
             newCompositeNode(grammarAccess.getSignedConstantRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSignedConstant=ruleSignedConstant();

            state._fsp--;

             current =iv_ruleSignedConstant; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSignedConstant"


    // $ANTLR start "ruleSignedConstant"
    // InternalJsonParser.g:2008:1: ruleSignedConstant returns [EObject current=null] : ( ( (lv_op_0_0= rulePlusMinus ) ) ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) ) ) ;
    public final EObject ruleSignedConstant() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_op_0_0 = null;

        EObject lv_ownedPropertyExpression_1_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:2011:28: ( ( ( (lv_op_0_0= rulePlusMinus ) ) ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) ) ) )
            // InternalJsonParser.g:2012:1: ( ( (lv_op_0_0= rulePlusMinus ) ) ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) ) )
            {
            // InternalJsonParser.g:2012:1: ( ( (lv_op_0_0= rulePlusMinus ) ) ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) ) )
            // InternalJsonParser.g:2012:2: ( (lv_op_0_0= rulePlusMinus ) ) ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) )
            {
            // InternalJsonParser.g:2012:2: ( (lv_op_0_0= rulePlusMinus ) )
            // InternalJsonParser.g:2013:1: (lv_op_0_0= rulePlusMinus )
            {
            // InternalJsonParser.g:2013:1: (lv_op_0_0= rulePlusMinus )
            // InternalJsonParser.g:2014:3: lv_op_0_0= rulePlusMinus
            {
             
            	        newCompositeNode(grammarAccess.getSignedConstantAccess().getOpPlusMinusParserRuleCall_0_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_27);
            lv_op_0_0=rulePlusMinus();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getSignedConstantRule());
            	        }
                   		set(
                   			current, 
                   			"op",
                    		lv_op_0_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.PlusMinus");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalJsonParser.g:2030:2: ( (lv_ownedPropertyExpression_1_0= ruleConstantValue ) )
            // InternalJsonParser.g:2031:1: (lv_ownedPropertyExpression_1_0= ruleConstantValue )
            {
            // InternalJsonParser.g:2031:1: (lv_ownedPropertyExpression_1_0= ruleConstantValue )
            // InternalJsonParser.g:2032:3: lv_ownedPropertyExpression_1_0= ruleConstantValue
            {
             
            	        newCompositeNode(grammarAccess.getSignedConstantAccess().getOwnedPropertyExpressionConstantValueParserRuleCall_1_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_2);
            lv_ownedPropertyExpression_1_0=ruleConstantValue();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getSignedConstantRule());
            	        }
                   		add(
                   			current, 
                   			"ownedPropertyExpression",
                    		lv_ownedPropertyExpression_1_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.ConstantValue");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSignedConstant"


    // $ANTLR start "entryRuleIntegerTerm"
    // InternalJsonParser.g:2056:1: entryRuleIntegerTerm returns [EObject current=null] : iv_ruleIntegerTerm= ruleIntegerTerm EOF ;
    public final EObject entryRuleIntegerTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIntegerTerm = null;


        try {
            // InternalJsonParser.g:2057:2: (iv_ruleIntegerTerm= ruleIntegerTerm EOF )
            // InternalJsonParser.g:2058:2: iv_ruleIntegerTerm= ruleIntegerTerm EOF
            {
             newCompositeNode(grammarAccess.getIntegerTermRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleIntegerTerm=ruleIntegerTerm();

            state._fsp--;

             current =iv_ruleIntegerTerm; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleIntegerTerm"


    // $ANTLR start "ruleIntegerTerm"
    // InternalJsonParser.g:2065:1: ruleIntegerTerm returns [EObject current=null] : ( ( (lv_value_0_0= ruleSignedInt ) ) ( (otherlv_1= RULE_ID ) )? ) ;
    public final EObject ruleIntegerTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        AntlrDatatypeRuleToken lv_value_0_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:2068:28: ( ( ( (lv_value_0_0= ruleSignedInt ) ) ( (otherlv_1= RULE_ID ) )? ) )
            // InternalJsonParser.g:2069:1: ( ( (lv_value_0_0= ruleSignedInt ) ) ( (otherlv_1= RULE_ID ) )? )
            {
            // InternalJsonParser.g:2069:1: ( ( (lv_value_0_0= ruleSignedInt ) ) ( (otherlv_1= RULE_ID ) )? )
            // InternalJsonParser.g:2069:2: ( (lv_value_0_0= ruleSignedInt ) ) ( (otherlv_1= RULE_ID ) )?
            {
            // InternalJsonParser.g:2069:2: ( (lv_value_0_0= ruleSignedInt ) )
            // InternalJsonParser.g:2070:1: (lv_value_0_0= ruleSignedInt )
            {
            // InternalJsonParser.g:2070:1: (lv_value_0_0= ruleSignedInt )
            // InternalJsonParser.g:2071:3: lv_value_0_0= ruleSignedInt
            {
             
            	        newCompositeNode(grammarAccess.getIntegerTermAccess().getValueSignedIntParserRuleCall_0_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_28);
            lv_value_0_0=ruleSignedInt();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getIntegerTermRule());
            	        }
                   		set(
                   			current, 
                   			"value",
                    		lv_value_0_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.SignedInt");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalJsonParser.g:2087:2: ( (otherlv_1= RULE_ID ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==RULE_ID) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // InternalJsonParser.g:2088:1: (otherlv_1= RULE_ID )
                    {
                    // InternalJsonParser.g:2088:1: (otherlv_1= RULE_ID )
                    // InternalJsonParser.g:2089:3: otherlv_1= RULE_ID
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getIntegerTermRule());
                    	        }
                            
                    otherlv_1=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_2); 

                    		newLeafNode(otherlv_1, grammarAccess.getIntegerTermAccess().getUnitUnitLiteralCrossReference_1_0()); 
                    	

                    }


                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleIntegerTerm"


    // $ANTLR start "entryRuleSignedInt"
    // InternalJsonParser.g:2108:1: entryRuleSignedInt returns [String current=null] : iv_ruleSignedInt= ruleSignedInt EOF ;
    public final String entryRuleSignedInt() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSignedInt = null;


        try {
            // InternalJsonParser.g:2109:1: (iv_ruleSignedInt= ruleSignedInt EOF )
            // InternalJsonParser.g:2110:2: iv_ruleSignedInt= ruleSignedInt EOF
            {
             newCompositeNode(grammarAccess.getSignedIntRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSignedInt=ruleSignedInt();

            state._fsp--;

             current =iv_ruleSignedInt.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSignedInt"


    // $ANTLR start "ruleSignedInt"
    // InternalJsonParser.g:2117:1: ruleSignedInt returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (kw= PlusSign | kw= HyphenMinus )? this_INTEGER_LIT_2= RULE_INTEGER_LIT ) ;
    public final AntlrDatatypeRuleToken ruleSignedInt() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_INTEGER_LIT_2=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:2121:6: ( ( (kw= PlusSign | kw= HyphenMinus )? this_INTEGER_LIT_2= RULE_INTEGER_LIT ) )
            // InternalJsonParser.g:2122:1: ( (kw= PlusSign | kw= HyphenMinus )? this_INTEGER_LIT_2= RULE_INTEGER_LIT )
            {
            // InternalJsonParser.g:2122:1: ( (kw= PlusSign | kw= HyphenMinus )? this_INTEGER_LIT_2= RULE_INTEGER_LIT )
            // InternalJsonParser.g:2122:2: (kw= PlusSign | kw= HyphenMinus )? this_INTEGER_LIT_2= RULE_INTEGER_LIT
            {
            // InternalJsonParser.g:2122:2: (kw= PlusSign | kw= HyphenMinus )?
            int alt28=3;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==PlusSign) ) {
                alt28=1;
            }
            else if ( (LA28_0==HyphenMinus) ) {
                alt28=2;
            }
            switch (alt28) {
                case 1 :
                    // InternalJsonParser.g:2123:2: kw= PlusSign
                    {
                    kw=(Token)match(input,PlusSign,FollowSets000.FOLLOW_24); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getSignedIntAccess().getPlusSignKeyword_0_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalJsonParser.g:2130:2: kw= HyphenMinus
                    {
                    kw=(Token)match(input,HyphenMinus,FollowSets000.FOLLOW_24); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getSignedIntAccess().getHyphenMinusKeyword_0_1()); 
                        

                    }
                    break;

            }

            this_INTEGER_LIT_2=(Token)match(input,RULE_INTEGER_LIT,FollowSets000.FOLLOW_2); 

            		current.merge(this_INTEGER_LIT_2);
                
             
                newLeafNode(this_INTEGER_LIT_2, grammarAccess.getSignedIntAccess().getINTEGER_LITTerminalRuleCall_1()); 
                

            }


            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSignedInt"


    // $ANTLR start "entryRuleRealTerm"
    // InternalJsonParser.g:2150:1: entryRuleRealTerm returns [EObject current=null] : iv_ruleRealTerm= ruleRealTerm EOF ;
    public final EObject entryRuleRealTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRealTerm = null;


        try {
            // InternalJsonParser.g:2151:2: (iv_ruleRealTerm= ruleRealTerm EOF )
            // InternalJsonParser.g:2152:2: iv_ruleRealTerm= ruleRealTerm EOF
            {
             newCompositeNode(grammarAccess.getRealTermRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleRealTerm=ruleRealTerm();

            state._fsp--;

             current =iv_ruleRealTerm; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRealTerm"


    // $ANTLR start "ruleRealTerm"
    // InternalJsonParser.g:2159:1: ruleRealTerm returns [EObject current=null] : ( ( (lv_value_0_0= ruleSignedReal ) ) ( (otherlv_1= RULE_ID ) )? ) ;
    public final EObject ruleRealTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        AntlrDatatypeRuleToken lv_value_0_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:2162:28: ( ( ( (lv_value_0_0= ruleSignedReal ) ) ( (otherlv_1= RULE_ID ) )? ) )
            // InternalJsonParser.g:2163:1: ( ( (lv_value_0_0= ruleSignedReal ) ) ( (otherlv_1= RULE_ID ) )? )
            {
            // InternalJsonParser.g:2163:1: ( ( (lv_value_0_0= ruleSignedReal ) ) ( (otherlv_1= RULE_ID ) )? )
            // InternalJsonParser.g:2163:2: ( (lv_value_0_0= ruleSignedReal ) ) ( (otherlv_1= RULE_ID ) )?
            {
            // InternalJsonParser.g:2163:2: ( (lv_value_0_0= ruleSignedReal ) )
            // InternalJsonParser.g:2164:1: (lv_value_0_0= ruleSignedReal )
            {
            // InternalJsonParser.g:2164:1: (lv_value_0_0= ruleSignedReal )
            // InternalJsonParser.g:2165:3: lv_value_0_0= ruleSignedReal
            {
             
            	        newCompositeNode(grammarAccess.getRealTermAccess().getValueSignedRealParserRuleCall_0_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_28);
            lv_value_0_0=ruleSignedReal();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getRealTermRule());
            	        }
                   		set(
                   			current, 
                   			"value",
                    		lv_value_0_0, 
                    		"com.collins.trustedsystems.briefcase.json.Json.SignedReal");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalJsonParser.g:2181:2: ( (otherlv_1= RULE_ID ) )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==RULE_ID) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // InternalJsonParser.g:2182:1: (otherlv_1= RULE_ID )
                    {
                    // InternalJsonParser.g:2182:1: (otherlv_1= RULE_ID )
                    // InternalJsonParser.g:2183:3: otherlv_1= RULE_ID
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getRealTermRule());
                    	        }
                            
                    otherlv_1=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_2); 

                    		newLeafNode(otherlv_1, grammarAccess.getRealTermAccess().getUnitUnitLiteralCrossReference_1_0()); 
                    	

                    }


                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRealTerm"


    // $ANTLR start "entryRuleNumericRangeTerm"
    // InternalJsonParser.g:2202:1: entryRuleNumericRangeTerm returns [EObject current=null] : iv_ruleNumericRangeTerm= ruleNumericRangeTerm EOF ;
    public final EObject entryRuleNumericRangeTerm() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNumericRangeTerm = null;


        try {
            // InternalJsonParser.g:2203:2: (iv_ruleNumericRangeTerm= ruleNumericRangeTerm EOF )
            // InternalJsonParser.g:2204:2: iv_ruleNumericRangeTerm= ruleNumericRangeTerm EOF
            {
             newCompositeNode(grammarAccess.getNumericRangeTermRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNumericRangeTerm=ruleNumericRangeTerm();

            state._fsp--;

             current =iv_ruleNumericRangeTerm; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNumericRangeTerm"


    // $ANTLR start "ruleNumericRangeTerm"
    // InternalJsonParser.g:2211:1: ruleNumericRangeTerm returns [EObject current=null] : ( ( (lv_minimum_0_0= ruleNumAlt ) ) otherlv_1= FullStopFullStop ( (lv_maximum_2_0= ruleNumAlt ) ) (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )? ) ;
    public final EObject ruleNumericRangeTerm() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_minimum_0_0 = null;

        EObject lv_maximum_2_0 = null;

        EObject lv_delta_4_0 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:2214:28: ( ( ( (lv_minimum_0_0= ruleNumAlt ) ) otherlv_1= FullStopFullStop ( (lv_maximum_2_0= ruleNumAlt ) ) (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )? ) )
            // InternalJsonParser.g:2215:1: ( ( (lv_minimum_0_0= ruleNumAlt ) ) otherlv_1= FullStopFullStop ( (lv_maximum_2_0= ruleNumAlt ) ) (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )? )
            {
            // InternalJsonParser.g:2215:1: ( ( (lv_minimum_0_0= ruleNumAlt ) ) otherlv_1= FullStopFullStop ( (lv_maximum_2_0= ruleNumAlt ) ) (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )? )
            // InternalJsonParser.g:2215:2: ( (lv_minimum_0_0= ruleNumAlt ) ) otherlv_1= FullStopFullStop ( (lv_maximum_2_0= ruleNumAlt ) ) (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )?
            {
            // InternalJsonParser.g:2215:2: ( (lv_minimum_0_0= ruleNumAlt ) )
            // InternalJsonParser.g:2216:1: (lv_minimum_0_0= ruleNumAlt )
            {
            // InternalJsonParser.g:2216:1: (lv_minimum_0_0= ruleNumAlt )
            // InternalJsonParser.g:2217:3: lv_minimum_0_0= ruleNumAlt
            {
             
            	        newCompositeNode(grammarAccess.getNumericRangeTermAccess().getMinimumNumAltParserRuleCall_0_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_29);
            lv_minimum_0_0=ruleNumAlt();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getNumericRangeTermRule());
            	        }
                   		set(
                   			current, 
                   			"minimum",
                    		lv_minimum_0_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.NumAlt");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,FullStopFullStop,FollowSets000.FOLLOW_27); 

                	newLeafNode(otherlv_1, grammarAccess.getNumericRangeTermAccess().getFullStopFullStopKeyword_1());
                
            // InternalJsonParser.g:2238:1: ( (lv_maximum_2_0= ruleNumAlt ) )
            // InternalJsonParser.g:2239:1: (lv_maximum_2_0= ruleNumAlt )
            {
            // InternalJsonParser.g:2239:1: (lv_maximum_2_0= ruleNumAlt )
            // InternalJsonParser.g:2240:3: lv_maximum_2_0= ruleNumAlt
            {
             
            	        newCompositeNode(grammarAccess.getNumericRangeTermAccess().getMaximumNumAltParserRuleCall_2_0()); 
            	    
            pushFollow(FollowSets000.FOLLOW_30);
            lv_maximum_2_0=ruleNumAlt();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getNumericRangeTermRule());
            	        }
                   		set(
                   			current, 
                   			"maximum",
                    		lv_maximum_2_0, 
                    		"org.osate.xtext.aadl2.properties.Properties.NumAlt");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalJsonParser.g:2256:2: (otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) ) )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==Delta) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // InternalJsonParser.g:2257:2: otherlv_3= Delta ( (lv_delta_4_0= ruleNumAlt ) )
                    {
                    otherlv_3=(Token)match(input,Delta,FollowSets000.FOLLOW_27); 

                        	newLeafNode(otherlv_3, grammarAccess.getNumericRangeTermAccess().getDeltaKeyword_3_0());
                        
                    // InternalJsonParser.g:2261:1: ( (lv_delta_4_0= ruleNumAlt ) )
                    // InternalJsonParser.g:2262:1: (lv_delta_4_0= ruleNumAlt )
                    {
                    // InternalJsonParser.g:2262:1: (lv_delta_4_0= ruleNumAlt )
                    // InternalJsonParser.g:2263:3: lv_delta_4_0= ruleNumAlt
                    {
                     
                    	        newCompositeNode(grammarAccess.getNumericRangeTermAccess().getDeltaNumAltParserRuleCall_3_1_0()); 
                    	    
                    pushFollow(FollowSets000.FOLLOW_2);
                    lv_delta_4_0=ruleNumAlt();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getNumericRangeTermRule());
                    	        }
                           		set(
                           			current, 
                           			"delta",
                            		lv_delta_4_0, 
                            		"org.osate.xtext.aadl2.properties.Properties.NumAlt");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNumericRangeTerm"


    // $ANTLR start "entryRuleNumAlt"
    // InternalJsonParser.g:2287:1: entryRuleNumAlt returns [EObject current=null] : iv_ruleNumAlt= ruleNumAlt EOF ;
    public final EObject entryRuleNumAlt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNumAlt = null;


        try {
            // InternalJsonParser.g:2288:2: (iv_ruleNumAlt= ruleNumAlt EOF )
            // InternalJsonParser.g:2289:2: iv_ruleNumAlt= ruleNumAlt EOF
            {
             newCompositeNode(grammarAccess.getNumAltRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleNumAlt=ruleNumAlt();

            state._fsp--;

             current =iv_ruleNumAlt; 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNumAlt"


    // $ANTLR start "ruleNumAlt"
    // InternalJsonParser.g:2296:1: ruleNumAlt returns [EObject current=null] : (this_RealTerm_0= ruleRealTerm | this_IntegerTerm_1= ruleIntegerTerm | this_SignedConstant_2= ruleSignedConstant | this_ConstantValue_3= ruleConstantValue ) ;
    public final EObject ruleNumAlt() throws RecognitionException {
        EObject current = null;

        EObject this_RealTerm_0 = null;

        EObject this_IntegerTerm_1 = null;

        EObject this_SignedConstant_2 = null;

        EObject this_ConstantValue_3 = null;


         enterRule(); 
            
        try {
            // InternalJsonParser.g:2299:28: ( (this_RealTerm_0= ruleRealTerm | this_IntegerTerm_1= ruleIntegerTerm | this_SignedConstant_2= ruleSignedConstant | this_ConstantValue_3= ruleConstantValue ) )
            // InternalJsonParser.g:2300:1: (this_RealTerm_0= ruleRealTerm | this_IntegerTerm_1= ruleIntegerTerm | this_SignedConstant_2= ruleSignedConstant | this_ConstantValue_3= ruleConstantValue )
            {
            // InternalJsonParser.g:2300:1: (this_RealTerm_0= ruleRealTerm | this_IntegerTerm_1= ruleIntegerTerm | this_SignedConstant_2= ruleSignedConstant | this_ConstantValue_3= ruleConstantValue )
            int alt31=4;
            switch ( input.LA(1) ) {
            case RULE_REAL_NUMBER:
                {
                alt31=1;
                }
                break;
            case PlusSign:
                {
                int LA31_2 = input.LA(2);

                if ( (LA31_2==RULE_INTEGER_LIT) ) {
                    alt31=2;
                }
                else if ( (LA31_2==RULE_ID) ) {
                    alt31=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 2, input);

                    throw nvae;
                }
                }
                break;
            case HyphenMinus:
                {
                int LA31_3 = input.LA(2);

                if ( (LA31_3==RULE_ID) ) {
                    alt31=3;
                }
                else if ( (LA31_3==RULE_INTEGER_LIT) ) {
                    alt31=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 3, input);

                    throw nvae;
                }
                }
                break;
            case RULE_INTEGER_LIT:
                {
                alt31=2;
                }
                break;
            case RULE_ID:
                {
                alt31=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // InternalJsonParser.g:2301:5: this_RealTerm_0= ruleRealTerm
                    {
                     
                            newCompositeNode(grammarAccess.getNumAltAccess().getRealTermParserRuleCall_0()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_RealTerm_0=ruleRealTerm();

                    state._fsp--;


                            current = this_RealTerm_0;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalJsonParser.g:2311:5: this_IntegerTerm_1= ruleIntegerTerm
                    {
                     
                            newCompositeNode(grammarAccess.getNumAltAccess().getIntegerTermParserRuleCall_1()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_IntegerTerm_1=ruleIntegerTerm();

                    state._fsp--;


                            current = this_IntegerTerm_1;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // InternalJsonParser.g:2321:5: this_SignedConstant_2= ruleSignedConstant
                    {
                     
                            newCompositeNode(grammarAccess.getNumAltAccess().getSignedConstantParserRuleCall_2()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_SignedConstant_2=ruleSignedConstant();

                    state._fsp--;


                            current = this_SignedConstant_2;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // InternalJsonParser.g:2331:5: this_ConstantValue_3= ruleConstantValue
                    {
                     
                            newCompositeNode(grammarAccess.getNumAltAccess().getConstantValueParserRuleCall_3()); 
                        
                    pushFollow(FollowSets000.FOLLOW_2);
                    this_ConstantValue_3=ruleConstantValue();

                    state._fsp--;


                            current = this_ConstantValue_3;
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNumAlt"


    // $ANTLR start "entryRuleAppliesToKeywords"
    // InternalJsonParser.g:2347:1: entryRuleAppliesToKeywords returns [String current=null] : iv_ruleAppliesToKeywords= ruleAppliesToKeywords EOF ;
    public final String entryRuleAppliesToKeywords() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAppliesToKeywords = null;


        try {
            // InternalJsonParser.g:2348:1: (iv_ruleAppliesToKeywords= ruleAppliesToKeywords EOF )
            // InternalJsonParser.g:2349:2: iv_ruleAppliesToKeywords= ruleAppliesToKeywords EOF
            {
             newCompositeNode(grammarAccess.getAppliesToKeywordsRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleAppliesToKeywords=ruleAppliesToKeywords();

            state._fsp--;

             current =iv_ruleAppliesToKeywords.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAppliesToKeywords"


    // $ANTLR start "ruleAppliesToKeywords"
    // InternalJsonParser.g:2356:1: ruleAppliesToKeywords returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= Applies kw= To ) ;
    public final AntlrDatatypeRuleToken ruleAppliesToKeywords() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:2360:6: ( (kw= Applies kw= To ) )
            // InternalJsonParser.g:2361:1: (kw= Applies kw= To )
            {
            // InternalJsonParser.g:2361:1: (kw= Applies kw= To )
            // InternalJsonParser.g:2362:2: kw= Applies kw= To
            {
            kw=(Token)match(input,Applies,FollowSets000.FOLLOW_31); 

                    current.merge(kw);
                    newLeafNode(kw, grammarAccess.getAppliesToKeywordsAccess().getAppliesKeyword_0()); 
                
            kw=(Token)match(input,To,FollowSets000.FOLLOW_2); 

                    current.merge(kw);
                    newLeafNode(kw, grammarAccess.getAppliesToKeywordsAccess().getToKeyword_1()); 
                

            }


            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAppliesToKeywords"


    // $ANTLR start "entryRuleInBindingKeywords"
    // InternalJsonParser.g:2381:1: entryRuleInBindingKeywords returns [String current=null] : iv_ruleInBindingKeywords= ruleInBindingKeywords EOF ;
    public final String entryRuleInBindingKeywords() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleInBindingKeywords = null;


        try {
            // InternalJsonParser.g:2382:1: (iv_ruleInBindingKeywords= ruleInBindingKeywords EOF )
            // InternalJsonParser.g:2383:2: iv_ruleInBindingKeywords= ruleInBindingKeywords EOF
            {
             newCompositeNode(grammarAccess.getInBindingKeywordsRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleInBindingKeywords=ruleInBindingKeywords();

            state._fsp--;

             current =iv_ruleInBindingKeywords.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleInBindingKeywords"


    // $ANTLR start "ruleInBindingKeywords"
    // InternalJsonParser.g:2390:1: ruleInBindingKeywords returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= In kw= Binding ) ;
    public final AntlrDatatypeRuleToken ruleInBindingKeywords() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:2394:6: ( (kw= In kw= Binding ) )
            // InternalJsonParser.g:2395:1: (kw= In kw= Binding )
            {
            // InternalJsonParser.g:2395:1: (kw= In kw= Binding )
            // InternalJsonParser.g:2396:2: kw= In kw= Binding
            {
            kw=(Token)match(input,In,FollowSets000.FOLLOW_32); 

                    current.merge(kw);
                    newLeafNode(kw, grammarAccess.getInBindingKeywordsAccess().getInKeyword_0()); 
                
            kw=(Token)match(input,Binding,FollowSets000.FOLLOW_2); 

                    current.merge(kw);
                    newLeafNode(kw, grammarAccess.getInBindingKeywordsAccess().getBindingKeyword_1()); 
                

            }


            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleInBindingKeywords"


    // $ANTLR start "entryRuleInModesKeywords"
    // InternalJsonParser.g:2415:1: entryRuleInModesKeywords returns [String current=null] : iv_ruleInModesKeywords= ruleInModesKeywords EOF ;
    public final String entryRuleInModesKeywords() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleInModesKeywords = null;


        try {
            // InternalJsonParser.g:2416:1: (iv_ruleInModesKeywords= ruleInModesKeywords EOF )
            // InternalJsonParser.g:2417:2: iv_ruleInModesKeywords= ruleInModesKeywords EOF
            {
             newCompositeNode(grammarAccess.getInModesKeywordsRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleInModesKeywords=ruleInModesKeywords();

            state._fsp--;

             current =iv_ruleInModesKeywords.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleInModesKeywords"


    // $ANTLR start "ruleInModesKeywords"
    // InternalJsonParser.g:2424:1: ruleInModesKeywords returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= In kw= Modes ) ;
    public final AntlrDatatypeRuleToken ruleInModesKeywords() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:2428:6: ( (kw= In kw= Modes ) )
            // InternalJsonParser.g:2429:1: (kw= In kw= Modes )
            {
            // InternalJsonParser.g:2429:1: (kw= In kw= Modes )
            // InternalJsonParser.g:2430:2: kw= In kw= Modes
            {
            kw=(Token)match(input,In,FollowSets000.FOLLOW_33); 

                    current.merge(kw);
                    newLeafNode(kw, grammarAccess.getInModesKeywordsAccess().getInKeyword_0()); 
                
            kw=(Token)match(input,Modes,FollowSets000.FOLLOW_2); 

                    current.merge(kw);
                    newLeafNode(kw, grammarAccess.getInModesKeywordsAccess().getModesKeyword_1()); 
                

            }


            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleInModesKeywords"


    // $ANTLR start "entryRuleINTVALUE"
    // InternalJsonParser.g:2449:1: entryRuleINTVALUE returns [String current=null] : iv_ruleINTVALUE= ruleINTVALUE EOF ;
    public final String entryRuleINTVALUE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleINTVALUE = null;


        try {
            // InternalJsonParser.g:2450:1: (iv_ruleINTVALUE= ruleINTVALUE EOF )
            // InternalJsonParser.g:2451:2: iv_ruleINTVALUE= ruleINTVALUE EOF
            {
             newCompositeNode(grammarAccess.getINTVALUERule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleINTVALUE=ruleINTVALUE();

            state._fsp--;

             current =iv_ruleINTVALUE.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleINTVALUE"


    // $ANTLR start "ruleINTVALUE"
    // InternalJsonParser.g:2458:1: ruleINTVALUE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_INTEGER_LIT_0= RULE_INTEGER_LIT ;
    public final AntlrDatatypeRuleToken ruleINTVALUE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_INTEGER_LIT_0=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:2462:6: (this_INTEGER_LIT_0= RULE_INTEGER_LIT )
            // InternalJsonParser.g:2463:5: this_INTEGER_LIT_0= RULE_INTEGER_LIT
            {
            this_INTEGER_LIT_0=(Token)match(input,RULE_INTEGER_LIT,FollowSets000.FOLLOW_2); 

            		current.merge(this_INTEGER_LIT_0);
                
             
                newLeafNode(this_INTEGER_LIT_0, grammarAccess.getINTVALUEAccess().getINTEGER_LITTerminalRuleCall()); 
                

            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleINTVALUE"


    // $ANTLR start "entryRuleQPREF"
    // InternalJsonParser.g:2480:1: entryRuleQPREF returns [String current=null] : iv_ruleQPREF= ruleQPREF EOF ;
    public final String entryRuleQPREF() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQPREF = null;


        try {
            // InternalJsonParser.g:2481:1: (iv_ruleQPREF= ruleQPREF EOF )
            // InternalJsonParser.g:2482:2: iv_ruleQPREF= ruleQPREF EOF
            {
             newCompositeNode(grammarAccess.getQPREFRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleQPREF=ruleQPREF();

            state._fsp--;

             current =iv_ruleQPREF.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleQPREF"


    // $ANTLR start "ruleQPREF"
    // InternalJsonParser.g:2489:1: ruleQPREF returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= ColonColon this_ID_2= RULE_ID )? ) ;
    public final AntlrDatatypeRuleToken ruleQPREF() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:2493:6: ( (this_ID_0= RULE_ID (kw= ColonColon this_ID_2= RULE_ID )? ) )
            // InternalJsonParser.g:2494:1: (this_ID_0= RULE_ID (kw= ColonColon this_ID_2= RULE_ID )? )
            {
            // InternalJsonParser.g:2494:1: (this_ID_0= RULE_ID (kw= ColonColon this_ID_2= RULE_ID )? )
            // InternalJsonParser.g:2494:6: this_ID_0= RULE_ID (kw= ColonColon this_ID_2= RULE_ID )?
            {
            this_ID_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_34); 

            		current.merge(this_ID_0);
                
             
                newLeafNode(this_ID_0, grammarAccess.getQPREFAccess().getIDTerminalRuleCall_0()); 
                
            // InternalJsonParser.g:2501:1: (kw= ColonColon this_ID_2= RULE_ID )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==ColonColon) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // InternalJsonParser.g:2502:2: kw= ColonColon this_ID_2= RULE_ID
                    {
                    kw=(Token)match(input,ColonColon,FollowSets000.FOLLOW_13); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getQPREFAccess().getColonColonKeyword_1_0()); 
                        
                    this_ID_2=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_2); 

                    		current.merge(this_ID_2);
                        
                     
                        newLeafNode(this_ID_2, grammarAccess.getQPREFAccess().getIDTerminalRuleCall_1_1()); 
                        

                    }
                    break;

            }


            }


            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleQPREF"


    // $ANTLR start "entryRuleQCREF"
    // InternalJsonParser.g:2522:1: entryRuleQCREF returns [String current=null] : iv_ruleQCREF= ruleQCREF EOF ;
    public final String entryRuleQCREF() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQCREF = null;


        try {
            // InternalJsonParser.g:2523:1: (iv_ruleQCREF= ruleQCREF EOF )
            // InternalJsonParser.g:2524:2: iv_ruleQCREF= ruleQCREF EOF
            {
             newCompositeNode(grammarAccess.getQCREFRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleQCREF=ruleQCREF();

            state._fsp--;

             current =iv_ruleQCREF.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleQCREF"


    // $ANTLR start "ruleQCREF"
    // InternalJsonParser.g:2531:1: ruleQCREF returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_ID_0= RULE_ID kw= ColonColon )* this_ID_2= RULE_ID (kw= FullStop this_ID_4= RULE_ID )? ) ;
    public final AntlrDatatypeRuleToken ruleQCREF() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;
        Token this_ID_4=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:2535:6: ( ( (this_ID_0= RULE_ID kw= ColonColon )* this_ID_2= RULE_ID (kw= FullStop this_ID_4= RULE_ID )? ) )
            // InternalJsonParser.g:2536:1: ( (this_ID_0= RULE_ID kw= ColonColon )* this_ID_2= RULE_ID (kw= FullStop this_ID_4= RULE_ID )? )
            {
            // InternalJsonParser.g:2536:1: ( (this_ID_0= RULE_ID kw= ColonColon )* this_ID_2= RULE_ID (kw= FullStop this_ID_4= RULE_ID )? )
            // InternalJsonParser.g:2536:2: (this_ID_0= RULE_ID kw= ColonColon )* this_ID_2= RULE_ID (kw= FullStop this_ID_4= RULE_ID )?
            {
            // InternalJsonParser.g:2536:2: (this_ID_0= RULE_ID kw= ColonColon )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==RULE_ID) ) {
                    int LA33_1 = input.LA(2);

                    if ( (LA33_1==ColonColon) ) {
                        alt33=1;
                    }


                }


                switch (alt33) {
            	case 1 :
            	    // InternalJsonParser.g:2536:7: this_ID_0= RULE_ID kw= ColonColon
            	    {
            	    this_ID_0=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_35); 

            	    		current.merge(this_ID_0);
            	        
            	     
            	        newLeafNode(this_ID_0, grammarAccess.getQCREFAccess().getIDTerminalRuleCall_0_0()); 
            	        
            	    kw=(Token)match(input,ColonColon,FollowSets000.FOLLOW_13); 

            	            current.merge(kw);
            	            newLeafNode(kw, grammarAccess.getQCREFAccess().getColonColonKeyword_0_1()); 
            	        

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);

            this_ID_2=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_36); 

            		current.merge(this_ID_2);
                
             
                newLeafNode(this_ID_2, grammarAccess.getQCREFAccess().getIDTerminalRuleCall_1()); 
                
            // InternalJsonParser.g:2556:1: (kw= FullStop this_ID_4= RULE_ID )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==FullStop) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // InternalJsonParser.g:2557:2: kw= FullStop this_ID_4= RULE_ID
                    {
                    kw=(Token)match(input,FullStop,FollowSets000.FOLLOW_13); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getQCREFAccess().getFullStopKeyword_2_0()); 
                        
                    this_ID_4=(Token)match(input,RULE_ID,FollowSets000.FOLLOW_2); 

                    		current.merge(this_ID_4);
                        
                     
                        newLeafNode(this_ID_4, grammarAccess.getQCREFAccess().getIDTerminalRuleCall_2_1()); 
                        

                    }
                    break;

            }


            }


            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleQCREF"


    // $ANTLR start "entryRuleSTAR"
    // InternalJsonParser.g:2577:1: entryRuleSTAR returns [String current=null] : iv_ruleSTAR= ruleSTAR EOF ;
    public final String entryRuleSTAR() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSTAR = null;


        try {
            // InternalJsonParser.g:2578:1: (iv_ruleSTAR= ruleSTAR EOF )
            // InternalJsonParser.g:2579:2: iv_ruleSTAR= ruleSTAR EOF
            {
             newCompositeNode(grammarAccess.getSTARRule()); 
            pushFollow(FollowSets000.FOLLOW_1);
            iv_ruleSTAR=ruleSTAR();

            state._fsp--;

             current =iv_ruleSTAR.getText(); 
            match(input,EOF,FollowSets000.FOLLOW_2); 

            }

        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSTAR"


    // $ANTLR start "ruleSTAR"
    // InternalJsonParser.g:2586:1: ruleSTAR returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : kw= Asterisk ;
    public final AntlrDatatypeRuleToken ruleSTAR() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // InternalJsonParser.g:2590:6: (kw= Asterisk )
            // InternalJsonParser.g:2592:2: kw= Asterisk
            {
            kw=(Token)match(input,Asterisk,FollowSets000.FOLLOW_2); 

                    current.merge(kw);
                    newLeafNode(kw, grammarAccess.getSTARAccess().getAsteriskKeyword()); 
                

            }

             leaveRule();
                
        }
         
        	catch (RecognitionException re) { 
        	    recover(input,re); 
        	    appendSkippedTokens();
        	}
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSTAR"

    // Delegated rules


    protected DFA18 dfa18 = new DFA18(this);
    static final String dfa_1s = "\25\uffff";
    static final String dfa_2s = "\6\uffff\1\16\2\uffff\1\21\1\23\2\uffff\1\16\2\uffff\1\21\3\uffff\1\23";
    static final String dfa_3s = "\1\4\5\uffff\1\7\2\53\2\7\2\uffff\1\7\2\uffff\1\7\1\uffff\1\55\1\uffff\1\7";
    static final String dfa_4s = "\1\55\5\uffff\4\55\1\35\2\uffff\1\35\2\uffff\1\35\1\uffff\1\55\1\uffff\1\35";
    static final String dfa_5s = "\1\uffff\1\1\1\2\1\3\1\4\1\5\5\uffff\1\11\1\12\1\uffff\1\7\1\6\1\uffff\1\10\1\uffff\1\13\1\uffff";
    static final String dfa_6s = "\25\uffff}>";
    static final String[] dfa_7s = {
            "\1\3\1\2\3\uffff\1\4\1\uffff\1\14\2\uffff\1\14\6\uffff\1\13\2\uffff\1\7\1\uffff\1\10\3\uffff\1\1\4\uffff\1\6\1\5\6\uffff\1\11\1\uffff\1\12",
            "",
            "",
            "",
            "",
            "",
            "\1\16\10\uffff\1\17\2\uffff\1\16\2\uffff\1\16\2\uffff\1\16\3\uffff\1\16\17\uffff\1\15",
            "\1\11\1\uffff\1\17",
            "\1\11\1\uffff\1\17",
            "\1\21\10\uffff\1\17\2\uffff\1\21\2\uffff\1\21\2\uffff\1\21\3\uffff\1\21\17\uffff\1\20",
            "\1\23\10\uffff\1\17\1\22\1\uffff\1\23\2\uffff\1\23\2\uffff\1\23\3\uffff\1\23",
            "",
            "",
            "\1\16\10\uffff\1\17\2\uffff\1\16\2\uffff\1\16\2\uffff\1\16\3\uffff\1\16",
            "",
            "",
            "\1\21\10\uffff\1\17\2\uffff\1\21\2\uffff\1\21\2\uffff\1\21\3\uffff\1\21",
            "",
            "\1\24",
            "",
            "\1\23\10\uffff\1\17\2\uffff\1\23\2\uffff\1\23\2\uffff\1\23\3\uffff\1\23"
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final short[] dfa_2 = DFA.unpackEncodedString(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final char[] dfa_4 = DFA.unpackEncodedStringToUnsignedChars(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[] dfa_6 = DFA.unpackEncodedString(dfa_6s);
    static final short[][] dfa_7 = unpackEncodedStringArray(dfa_7s);

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = dfa_1;
            this.eof = dfa_2;
            this.min = dfa_3;
            this.max = dfa_4;
            this.accept = dfa_5;
            this.special = dfa_6;
            this.transition = dfa_7;
        }
        public String getDescription() {
            return "1150:1: (this_RecordTerm_0= ruleRecordTerm | this_ReferenceTerm_1= ruleReferenceTerm | this_ComponentClassifierTerm_2= ruleComponentClassifierTerm | this_ComputedTerm_3= ruleComputedTerm | this_StringTerm_4= ruleStringTerm | this_NumericRangeTerm_5= ruleNumericRangeTerm | this_RealTerm_6= ruleRealTerm | this_IntegerTerm_7= ruleIntegerTerm | this_ListTerm_8= ruleListTerm | this_BooleanLiteral_9= ruleBooleanLiteral | this_LiteralorReferenceTerm_10= ruleLiteralorReferenceTerm )";
        }
    }
 

    
    private static class FollowSets000 {
        public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000001200000000L});
        public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000202000000L});
        public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000001DC2006800L});
        public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000082000000L});
        public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000001D40006800L});
        public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000010000000L});
        public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000048000L});
        public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000281C45204A70L});
        public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000022080080L});
        public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000200000000000L});
        public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000022080000L});
        public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000200000L});
        public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000400000L});
        public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000020000000L});
        public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000000080002L});
        public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000002400000L});
        public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000200080000000L});
        public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000281C45604A70L});
        public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000000040000L});
        public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000048000002L});
        public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000080000000000L});
        public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000080010000L});
        public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000080000000L});
        public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000280C05000000L});
        public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000200000000002L});
        public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0000000000010000L});
        public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000000000000402L});
        public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000000000100000L});
        public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0000000000000100L});
        public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x0000000000001000L});
        public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x0000000000020002L});
        public static final BitSet FOLLOW_35 = new BitSet(new long[]{0x0000000000020000L});
        public static final BitSet FOLLOW_36 = new BitSet(new long[]{0x0000000008000002L});
    }


}