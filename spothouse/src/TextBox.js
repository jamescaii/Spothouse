import React, {useEffect, useRef, useState} from 'react';
import './App.css';
import './App.js';

/**
 * A functional component representing a TextBox
 * @param props = the inputted props from the parent
 * @return the TextBox element
 */
function TextBox(props) {
    const textInput = useRef(0);
    const [textInp, setText] = useState(0);
    const [searchQuery, setSearch] = useState("")

    /**
     * handleChange.
     * Changes our variables information on the box to the user text
     * @param props = the inputted props from the parent
     * @param event = the text of the user
     */
    function handleChange(props, event) {
        props.onChange(event.target.value)
    }

    /**
     * useEffect (for nearest clicking).
     * Forcibly changes the text of the box to the user click
     */
    useEffect(() => {
        textInput.current = props.force
        setText(props.force)
    }, [props.force]);

  return (
    <div className="TextBox">
      <div>{props.label}</div>
      <input 
        type={'text'}
        value={textInp}
        onChange={(e) => handleChange(props, e)}
      />
    </div>
  );
}

export default TextBox;
