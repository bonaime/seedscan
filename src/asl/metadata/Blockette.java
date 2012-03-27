/*
 * Copyright 2012, United States Geological Survey or
 * third-party contributors as indicated by the @author tags.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/  >.
 *
 */
package asl.metadata;

import java.util.Hashtable;

public class Blockette
{
    private int number;
    private Hashtable<Integer, Field> fields;

    private int lastStartID = 0;

    public Blockette(int number)
    {
        this.number = number;
        fields = new Hashtable<Integer, Field>();
    }

    public int getNumber()
    {
        return number;
    }

    public boolean addFieldData(String fieldIdentifier, String data)
    throws FieldIdentifierException
    {
        String[] range = fieldIdentifier.split("-");
        if (range.length < 1) {
            //throw new FieldIdentifierException("Invalid field identifier '" +fieldIdentifier+ "'"); 
            throw new FieldIdentifierException(); 
        }

        int start = Integer.parseInt(range[0]);
        String description = "";

        // The following determines if the field identifier is out of order.
        // We also use this to determine if a new Blockette was encountered
        // while parsing.
        // 
        // Some IDs can be out of order, so we add exceptions for those.
        if ((lastStartID > start) &&
            ((((number != 52) || 
               (lastStartID > 4)) &&
              (start == 3)) ||
             ((number == 52) && (start == 4)))) {
            return false;
        }

        Field field;
        int id = start;
        lastStartID = start;
        // We are dealing with multiple field identifiers
        if (range.length > 1) {
            int end = Integer.parseInt(range[1]);
            String[] dataItems = data.split("\\s");
            int index = dataItems.length - (end - start + 1);
            for (; index <= dataItems.length; index++) {
                if (!fields.containsKey(id)) {
                    field = new Field(id, description);
                    fields.put(id, field);
                }
                else {
                    field = fields.get(id);
                }
                field.addValue(dataItems[index]);
                id++;
            }
        }
        // We are only dealing with a single field identifier
        else {
            String[] parts = data.split(":", 1);
            String value;
            if (parts.length > 1) {
                description = parts[0];
                value = parts[1];
            }
            else {
                value = parts[0];
            }

            if (!fields.containsKey(id)) {
                field = new Field(id, description);
                fields.put(id, field);
            }
            else {
                field = fields.get(id);
            }
            field.addValue(value);
        }

        return true;
    }

}
